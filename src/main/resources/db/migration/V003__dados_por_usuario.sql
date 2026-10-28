-- Aplicar antes de executar a versao em que cada usuario enxerga somente os proprios dados.
-- Os registros existentes passam a pertencer a conta mais antiga (menor usuario_id).
-- Nenhum dado e apagado. Pode ser repetida.
BEGIN;
SET LOCAL lock_timeout = '10s';

DO $$
DECLARE
    dono integer := (SELECT min(usuario_id) FROM usuario);
    tabela text;
    pendentes bigint;
    r record;
BEGIN
    FOREACH tabela IN ARRAY ARRAY['pessoa', 'endereco', 'servico', 'peca', 'estoque', 'movimentacao_estoque',
            'ordem_servico', 'item_os', 'item_peca', 'venda', 'item_venda', 'pagamento_venda'] LOOP
        IF to_regclass(tabela) IS NULL THEN
            CONTINUE;
        END IF;
        EXECUTE format('ALTER TABLE %I ADD COLUMN IF NOT EXISTS usuario_id integer', tabela);
        IF dono IS NOT NULL THEN
            EXECUTE format('UPDATE %I SET usuario_id = $1 WHERE usuario_id IS NULL', tabela) USING dono;
        END IF;
        EXECUTE format('SELECT count(*) FROM %I WHERE usuario_id IS NULL', tabela) INTO pendentes;
        IF pendentes > 0 THEN
            RAISE EXCEPTION 'A tabela % tem registros mas nao ha nenhum usuario para assumi-los. Nenhum dado foi alterado.', tabela;
        END IF;
        EXECUTE format('CREATE INDEX IF NOT EXISTS ix_%s_usuario ON %I (usuario_id)', tabela, tabela);
    END LOOP;

    -- As chaves unicas globais passam a ser unicas por usuario.
    FOR r IN SELECT c.conrelid::regclass::text AS tabela, c.conname
             FROM pg_constraint c
             JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
             WHERE c.contype = 'u' AND array_length(c.conkey, 1) = 1
               AND ((c.conrelid = to_regclass('pessoa') AND a.attname = 'pessoa_documento')
                 OR (c.conrelid = to_regclass('ordem_servico') AND a.attname = 'ordem_servico_numero')
                 OR (c.conrelid = to_regclass('venda') AND a.attname = 'venda_numero')) LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.tabela, r.conname);
    END LOOP;

    IF to_regclass('pessoa') IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_pessoa_usuario_documento') THEN
        ALTER TABLE pessoa ADD CONSTRAINT uk_pessoa_usuario_documento UNIQUE (usuario_id, pessoa_documento);
    END IF;
    IF to_regclass('ordem_servico') IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_ordem_servico_usuario_numero') THEN
        ALTER TABLE ordem_servico ADD CONSTRAINT uk_ordem_servico_usuario_numero UNIQUE (usuario_id, ordem_servico_numero);
    END IF;
    IF to_regclass('venda') IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_venda_usuario_numero') THEN
        ALTER TABLE venda ADD CONSTRAINT uk_venda_usuario_numero UNIQUE (usuario_id, venda_numero);
    END IF;
END;
$$;
COMMIT;
