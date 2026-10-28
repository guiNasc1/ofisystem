param(
    [string]$PostgresBin = 'C:/Program Files/PostgreSQL/18/bin',
    [ValidateRange(1024, 65535)][int]$Porta = 55441
)

$ErrorActionPreference = 'Stop'
$projeto = Split-Path -Parent $PSScriptRoot
$pastaTeste = Join-Path $projeto 'target/testes-migracoes'
$dados = Join-Path $pastaTeste 'data'
New-Item -ItemType Directory -Path $pastaTeste -Force | Out-Null
foreach ($arquivo in @('initdb.exe', 'pg_ctl.exe', 'psql.exe')) {
    if (-not (Test-Path -LiteralPath (Join-Path $PostgresBin $arquivo))) {
        throw "PostgreSQL nao encontrado em $PostgresBin. Informe -PostgresBin."
    }
}
if (-not (Test-Path -LiteralPath (Join-Path $dados 'PG_VERSION'))) {
    & (Join-Path $PostgresBin 'initdb.exe') -D $dados -U ofisystem_teste --auth=trust --encoding=UTF8 --locale=C *> (Join-Path $pastaTeste 'initdb.log')
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao preparar PostgreSQL de testes. Veja target/testes-migracoes/initdb.log.' }
}

# Servidor exclusivo deste roteiro, limitado a 127.0.0.1; nenhuma configuracao do Supabase e lida.
$iniciado = $false
try {
    $argumentos = @('-D', ('"{0}"' -f $dados), '-l', ('"{0}"' -f (Join-Path $pastaTeste 'servidor.log')),
                    '-o', ('"-p {0} -h 127.0.0.1"' -f $Porta), '-w', 'start')
    $processo = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -ArgumentList $argumentos -WindowStyle Hidden -PassThru
    $processo.WaitForExit()
    if ($processo.ExitCode -ne 0) { throw 'Falha ao iniciar o servidor isolado. Confira a porta e servidor.log.' }
    $iniciado = $true
    foreach ($roteiro in @('corrigir_fk_item_os_test.sql', 'usuarios_escopo_academico_test.sql')) {
        $log = Join-Path $pastaTeste ($roteiro + '.log')
        & (Join-Path $PostgresBin 'psql.exe') -X -w -h 127.0.0.1 -p $Porta -U ofisystem_teste -d postgres -v ON_ERROR_STOP=1 -f (Join-Path $projeto "src/test/sql/$roteiro") *> $log
        if ($LASTEXITCODE -ne 0 -or -not (Select-String -LiteralPath $log -Pattern '^OK:' -Quiet)) {
            throw "Falha em $roteiro. Consulte $log."
        }
        Get-Content -LiteralPath $log | Select-String '^OK:' | ForEach-Object { $_.Line }
    }
} finally {
    if ($iniciado) {
        $processo = Start-Process -FilePath (Join-Path $PostgresBin 'pg_ctl.exe') -ArgumentList @('-D', ('"{0}"' -f $dados), '-m', 'fast', '-w', 'stop') -WindowStyle Hidden -PassThru
        $processo.WaitForExit()
        if ($processo.ExitCode -ne 0) { Write-Warning "Nao foi possivel encerrar o servidor de testes em $dados." }
    }
}
