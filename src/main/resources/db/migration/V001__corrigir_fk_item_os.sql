-- Executar uma vez no banco existente, antes de voltar a salvar ordens de servico.
-- O Hibernate update nao remove a FK antiga que aponta para item_nota.
BEGIN;
SET LOCAL lock_timeout = '10s';

DO $$
DECLARE
    tabela_item regclass := 'item_os'::regclass;
    tabela_os regclass := 'ordem_servico'::regclass;
    tabela_nota regclass := to_regclass('item_nota');
    coluna_item smallint;
    coluna_os smallint;
    restricao record;
    possui_orfaos boolean;
BEGIN
    -- Impede gravacoes concorrentes durante a verificacao e a troca da FK.
    EXECUTE format('LOCK TABLE %s IN ACCESS EXCLUSIVE MODE', tabela_item);

    SELECT attnum INTO STRICT coluna_item FROM pg_attribute
    WHERE attrelid = tabela_item AND attname = 'ordem_servico_id' AND NOT attisdropped;
    SELECT attnum INTO STRICT coluna_os FROM pg_attribute
    WHERE attrelid = tabela_os AND attname = 'ordem_servico_id' AND NOT attisdropped;

    EXECUTE format(
        'SELECT EXISTS (SELECT 1 FROM %s i LEFT JOIN %s o'
        ' ON o.ordem_servico_id = i.ordem_servico_id'
        ' WHERE i.ordem_servico_id IS NOT NULL AND o.ordem_servico_id IS NULL)',
        tabela_item, tabela_os)
    INTO possui_orfaos;

    IF possui_orfaos THEN
        RAISE EXCEPTION USING ERRCODE = '23503',
            MESSAGE = 'Existem itens de servico sem uma ordem_servico correspondente. Nenhum dado foi alterado.',
            HINT = 'Revise item_os.ordem_servico_id antes de executar novamente a migracao.';
    END IF;

    -- Remove somente o vinculo legado incorreto, independentemente do nome gerado.
    FOR restricao IN
        SELECT conname FROM pg_constraint
        WHERE contype = 'f' AND conrelid = tabela_item
          AND conkey = ARRAY[coluna_item] AND confrelid = tabela_nota
    LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', tabela_item, restricao.conname);
    END LOOP;

    -- Reutiliza e valida uma FK correta existente, evitando duplicatas ao repetir.
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE contype = 'f' AND conrelid = tabela_item AND confrelid = tabela_os
          AND conkey = ARRAY[coluna_item] AND confkey = ARRAY[coluna_os]
    ) THEN
        FOR restricao IN
            SELECT conname FROM pg_constraint
            WHERE contype = 'f' AND conrelid = tabela_item AND confrelid = tabela_os
              AND conkey = ARRAY[coluna_item] AND confkey = ARRAY[coluna_os]
              AND NOT convalidated
        LOOP
            EXECUTE format('ALTER TABLE %s VALIDATE CONSTRAINT %I', tabela_item, restricao.conname);
        END LOOP;
    ELSE
        EXECUTE format(
            'ALTER TABLE %s ADD CONSTRAINT fk_item_os_ordem_servico'
            ' FOREIGN KEY (ordem_servico_id) REFERENCES %s (ordem_servico_id)',
            tabela_item, tabela_os);
    END IF;
END;
$$;

COMMIT;
