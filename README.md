# ofisystem

Protótipo acadêmico desktop de gerenciamento de oficinas. Utiliza uma base única
de demonstração; o nome de oficina do usuário não separa dados por empresa.

## Executar e demonstrar

- Compilar com JDK 17 ou superior e Maven: `mvn package`.
- Executar a classe `com.ofisystem.Main` pela IDE, com as dependências do Maven.
- A configuração da base PostgreSQL está em `src/main/resources/META-INF/persistence.xml`.
  O uso do Supabase exige conexão com a rede.
- Em uma base vazia, a primeira conta é sempre **Administrador**. Nas demais, o perfil
  (Administrador ou Usuário) é escolhido na tela de cadastro. O início mostra o perfil
  de quem está conectado. O login usa o nome de usuário e ignora diferenças de maiúsculas.
- **Cada usuário enxerga somente os próprios dados** (clientes, serviços, peças, estoque,
  OS, vendas e relatórios). A numeração de OS e vendas e a unicidade de documento são
  por usuário. O perfil Administrador não dá acesso aos dados de outras contas.
- Em uma base existente, aplicar **V002 e V003 antes de iniciar esta versão**, conforme abaixo.

## Regras da versão acadêmica

- OS e vendas de balcão são independentes. Concluir a OS **não cria uma venda**.
- As peças são baixadas ao salvar a OS; edições ajustam somente a diferença.
- **Concluir e receber** confirma o pagamento integral em uma única forma. O valor
  entra nos recebimentos na data da conclusão. Não há parcelamento nem contas a receber.
- OS com itens precisa ter total positivo; desconto integral e serviços gratuitos
  estão fora do escopo. Um rascunho sem itens pode ser salvo, mas não concluído.
- Cancelar uma OS aberta pressupõe a devolução integral das peças. OS concluídas
  e canceladas ficam bloqueadas para edição.
- Não há emissão fiscal nem documento interno derivado da OS; a única impressão é a da própria OS.

A documentação revisada e os diagramas editáveis estão em [`docs/`](docs/).
O roteiro de apresentação está em [`docs/roteiro-demonstracao.md`](docs/roteiro-demonstracao.md).
O histórico completo das alterações, resultados e limites da verificação está em
[`docs/alteracoes-e-testes.md`](docs/alteracoes-e-testes.md).

## Testes isolados

`mvn test` executa os testes JUnit com H2 em memória, sem acessar o Supabase.
A suíte possui **27 testes**: 13 de integração dos fluxos, 13 de validação de
cadastro e um de geração de impressão. Cobre autenticação, perfis, duplicidades,
estoque, conclusão, cancelamento, descontos, venda de balcão e
relatórios combinados. As impressões de teste são geradas em `target/evidencias-testes/`.

Para compilar, testar e empacotar: `mvn verify`.

No Windows, com PostgreSQL 18 instalado, as duas suítes de migração podem ser
executadas em um servidor local exclusivo de testes:

```powershell
./scripts/testar-migracoes.ps1
```

O roteiro prepara uma base em `target/testes-migracoes/`, usa apenas `127.0.0.1`
na porta 55441 e encerra o servidor ao terminar. `-PostgresBin` e `-Porta` permitem
ajustar a instalação local. As mensagens de erro dos casos negativos são esperadas;
as asserções seguintes verificam a reversão, e cada suíte termina com `OK:`.

Para conferir a interface sem acessar o Supabase, execute pela IDE a classe de
teste `com.ofisystem.VerificacaoVisual`, com o classpath de testes. Ela abre as
telas reais de OS e financeiro sobre H2 em memória, com duas OS abertas e uma
venda de balcão fictícia. Ao fechar, os dados da sessão são descartados. O argumento
opcional `--sempre-visivel` mantém essa janela de teste em primeiro plano.

## Dados por usuário na base existente

Executar [`V003__dados_por_usuario.sql`](src/main/resources/db/migration/V003__dados_por_usuario.sql)
depois da V002. Ela acrescenta `usuario_id` às tabelas de dados, atribui todos os registros
existentes à conta mais antiga, e troca as chaves únicas globais de documento, número de OS
e número de venda por chaves únicas por usuário. Pode ser repetida.

## Atualização dos usuários da base existente

Executar no SQL Editor do banco de demonstração o conteúdo completo de
[`V002__usuarios_escopo_academico.sql`](src/main/resources/db/migration/V002__usuarios_escopo_academico.sql).
A migração normaliza nomes e e-mails, impede duplicidades, converte os perfis
GERENTE/SUPORTE e os perfis vazios para USUARIO e torna a conta mais antiga ADMIN
se ainda não houver administrador. As senhas e os usuários são preservados.
Se encontrar cadastros duplicados ou vazios, a migração cancela todas as alterações
e solicita a revisão desses cadastros. Pode ser repetida.

Para verificar a migração em um PostgreSQL descartável:

```sh
psql -X -v ON_ERROR_STOP=1 -d banco_de_testes -f src/test/sql/usuarios_escopo_academico_test.sql
```

## Correção do vínculo dos itens da OS

Se salvar uma OS falhar com `item_os` referenciando `item_nota`, execute no
banco usado pelo aplicativo o arquivo
[`V001__corrigir_fk_item_os.sql`](src/main/resources/db/migration/V001__corrigir_fk_item_os.sql).
No Supabase, copie o conteúdo completo para o SQL Editor e execute.

A migração troca a chave estrangeira incorreta por um vínculo com
`ordem_servico`, sem apagar registros. Pode ser executada novamente e preserva
uma chave correta que já exista. Se houver itens sem OS correspondente, a
transação é cancelada para que esses dados sejam revisados antes da correção.
Se o banco estiver ocupado, a espera pelo bloqueio é limitada a 10 segundos;
nesse caso, tente novamente quando não houver gravações.

O projeto usa `hibernate.hbm2ddl.auto=update`, que não substitui essa migração
do banco existente. Os arquivos em `db/migration` devem ser executados
manualmente; não há executor automático de migrações configurado.

Para verificar a correção em um banco PostgreSQL de testes descartável:

```sh
psql -X -v ON_ERROR_STOP=1 -d banco_de_testes -f src/test/sql/corrigir_fk_item_os_test.sql
```
