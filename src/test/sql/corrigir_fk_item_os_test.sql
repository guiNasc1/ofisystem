-- Rodar somente em um banco descartavel: cria e remove schemas de teste.
\set ON_ERROR_STOP on
CREATE SCHEMA teste_fk_item_os;
SET search_path TO teste_fk_item_os;
CREATE TABLE ordem_servico (ordem_servico_id integer PRIMARY KEY);
CREATE TABLE item_nota (item_nota_id integer PRIMARY KEY);
CREATE TABLE servico (servico_id integer PRIMARY KEY);
CREATE TABLE item_os (
    item_os_id integer PRIMARY KEY,
    ordem_servico_id integer NOT NULL,
    servico_id integer REFERENCES servico,
    CONSTRAINT fk_legada FOREIGN KEY (ordem_servico_id) REFERENCES item_nota
);
INSERT INTO ordem_servico VALUES (1), (10);
INSERT INTO item_nota VALUES (1);
INSERT INTO servico VALUES (1);
INSERT INTO item_os VALUES (1, 1, 1);

-- Reproduz a falha original: OS 10 existe, mas item_nota 10 nao.
DO $$
BEGIN
    BEGIN
        INSERT INTO item_os VALUES (2, 10, 1);
        RAISE EXCEPTION 'A FK legada deveria impedir a insercao';
    EXCEPTION WHEN foreign_key_violation THEN
        NULL;
    END;
END;
$$;

\ir ../../main/resources/db/migration/V001__corrigir_fk_item_os.sql
\ir ../../main/resources/db/migration/V001__corrigir_fk_item_os.sql
INSERT INTO item_os VALUES (2, 10, 1);

DO $$
BEGIN
    IF (SELECT count(*) FROM item_os) <> 2 THEN
        RAISE EXCEPTION 'A migracao deve preservar registros';
    END IF;
    IF (SELECT count(*) FROM pg_constraint
        WHERE conrelid = 'item_os'::regclass AND contype = 'f') <> 2 THEN
        RAISE EXCEPTION 'A repeticao nao deve duplicar FKs nem remover a FK de servico';
    END IF;
    BEGIN
        INSERT INTO item_os VALUES (3, 999, 1);
        RAISE EXCEPTION 'Uma OS inexistente deve ser rejeitada';
    EXCEPTION WHEN foreign_key_violation THEN
        NULL;
    END;
END;
$$;

-- Uma base que ja tem a FK correta deve manter o nome e validar a restricao.
ALTER TABLE item_os RENAME CONSTRAINT fk_item_os_ordem_servico TO fk_correta_existente;
ALTER TABLE item_os DROP CONSTRAINT fk_correta_existente;
ALTER TABLE item_os ADD CONSTRAINT fk_correta_existente
    FOREIGN KEY (ordem_servico_id) REFERENCES ordem_servico NOT VALID;
\ir ../../main/resources/db/migration/V001__corrigir_fk_item_os.sql
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
                   WHERE conrelid = 'item_os'::regclass
                     AND conname = 'fk_correta_existente' AND convalidated) THEN
        RAISE EXCEPTION 'A FK correta existente deve ser preservada e validada';
    END IF;
END;
$$;

-- Uma base inconsistente deve abortar sem remover a FK legada ou os dados.
CREATE SCHEMA teste_fk_item_os_orfao;
SET search_path TO teste_fk_item_os_orfao;
CREATE TABLE ordem_servico (ordem_servico_id integer PRIMARY KEY);
CREATE TABLE item_nota (item_nota_id integer PRIMARY KEY);
CREATE TABLE item_os (
    item_os_id integer PRIMARY KEY,
    ordem_servico_id integer NOT NULL,
    CONSTRAINT fk_legada FOREIGN KEY (ordem_servico_id) REFERENCES item_nota
);
INSERT INTO item_nota VALUES (99);
INSERT INTO item_os VALUES (1, 99);
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V001__corrigir_fk_item_os.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF (SELECT count(*) FROM item_os) <> 1 OR NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'item_os'::regclass AND conname = 'fk_legada'
          AND confrelid = 'item_nota'::regclass
    ) THEN
        RAISE EXCEPTION 'A falha deve manter o estado original';
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint
               WHERE conrelid = 'item_os'::regclass AND confrelid = 'ordem_servico'::regclass) THEN
        RAISE EXCEPTION 'A migracao com orfaos nao deve criar a nova FK';
    END IF;
END;
$$;

SET search_path TO public;
DROP SCHEMA teste_fk_item_os CASCADE;
DROP SCHEMA teste_fk_item_os_orfao CASCADE;
\echo 'OK: falha reproduzida, FK corrigida, repeticao, preservacao e rollback verificados.'
