-- Executar somente em uma base PostgreSQL descartavel.
\set ON_ERROR_STOP on
CREATE SCHEMA teste_dados_por_usuario;
SET search_path TO teste_dados_por_usuario;
CREATE TABLE usuario (usuario_id integer PRIMARY KEY);
CREATE TABLE pessoa (pessoa_id integer PRIMARY KEY, pessoa_documento varchar(20) NOT NULL UNIQUE);
CREATE TABLE ordem_servico (ordem_servico_id integer PRIMARY KEY, ordem_servico_numero varchar(10) NOT NULL,
    CONSTRAINT uk_legado_os UNIQUE (ordem_servico_numero));
CREATE TABLE venda (venda_id integer PRIMARY KEY, venda_numero varchar(10) NOT NULL UNIQUE);
CREATE TABLE peca (peca_id integer PRIMARY KEY);
INSERT INTO usuario VALUES (7), (3), (9);
INSERT INTO pessoa VALUES (1, '52998224725'), (2, '11144477735');
INSERT INTO ordem_servico VALUES (1, '000001');
INSERT INTO venda VALUES (1, '000001');
INSERT INTO peca VALUES (1);
\ir ../../main/resources/db/migration/V003__dados_por_usuario.sql
\ir ../../main/resources/db/migration/V003__dados_por_usuario.sql
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pessoa WHERE usuario_id IS DISTINCT FROM 3)
       OR EXISTS (SELECT 1 FROM ordem_servico WHERE usuario_id IS DISTINCT FROM 3)
       OR EXISTS (SELECT 1 FROM venda WHERE usuario_id IS DISTINCT FROM 3)
       OR EXISTS (SELECT 1 FROM peca WHERE usuario_id IS DISTINCT FROM 3) THEN
        RAISE EXCEPTION 'Os registros existentes devem pertencer a conta mais antiga';
    END IF;
    -- Outro usuario pode repetir documento e numero; o mesmo usuario nao.
    INSERT INTO pessoa VALUES (3, '52998224725', 9);
    INSERT INTO ordem_servico VALUES (2, '000001', 9);
    INSERT INTO venda VALUES (2, '000001', 9);
    BEGIN
        INSERT INTO pessoa VALUES (4, '52998224725', 9);
        RAISE EXCEPTION 'Documento repetido no mesmo usuario deve ser rejeitado';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
    BEGIN
        INSERT INTO ordem_servico VALUES (3, '000001', 9);
        RAISE EXCEPTION 'Numero repetido no mesmo usuario deve ser rejeitado';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
    IF (SELECT count(*) FROM pg_constraint WHERE contype = 'u'
        AND conrelid IN ('pessoa'::regclass, 'ordem_servico'::regclass, 'venda'::regclass)) <> 3 THEN
        RAISE EXCEPTION 'Devem restar somente as tres chaves unicas por usuario';
    END IF;
END;
$$;

-- Registros sem nenhum usuario para assumi-los interrompem a migracao sem alterar dados.
DROP TABLE peca;
DROP TABLE venda;
DROP TABLE ordem_servico;
DROP TABLE pessoa;
CREATE TABLE pessoa (pessoa_id integer PRIMARY KEY, pessoa_documento varchar(20) NOT NULL UNIQUE);
INSERT INTO pessoa VALUES (1, '52998224725');
DELETE FROM usuario;
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V003__dados_por_usuario.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'teste_dados_por_usuario' AND table_name = 'pessoa'
                 AND column_name = 'usuario_id') THEN
        RAISE EXCEPTION 'Sem usuario para assumir os dados, a migracao deve desfazer tudo';
    END IF;
END;
$$;
SELECT 'OK: migracao V003 verificada' AS resultado;
DROP SCHEMA teste_dados_por_usuario CASCADE;
