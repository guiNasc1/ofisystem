-- Executar somente em uma base PostgreSQL descartavel.
\set ON_ERROR_STOP on
CREATE SCHEMA teste_usuarios_academico;
SET search_path TO teste_usuarios_academico;
CREATE TABLE usuario (
    usuario_id integer PRIMARY KEY,
    usuario_nome varchar(255) NOT NULL,
    usuario_email varchar(255) NOT NULL,
    usuario_perfil varchar(255),
    usuario_senha varchar(255) DEFAULT 'hash-ficticio-preservado'
);
INSERT INTO usuario (usuario_id, usuario_nome, usuario_email, usuario_perfil) VALUES
    (1, ' Ariel ', ' ARIEL@exemplo.com ', NULL),
    (2, 'Colega', 'colega@exemplo.com', 'GERENTE'),
    (9, 'Suporte', 'suporte@example.com', 'SUPORTE');
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_id = 1 AND usuario_nome = 'ariel'
                   AND usuario_email = 'ariel@exemplo.com' AND usuario_perfil = 'ADMIN') THEN
        RAISE EXCEPTION 'A primeira conta deve ser normalizada e receber ADMIN';
    END IF;
    IF (SELECT usuario_perfil FROM usuario WHERE usuario_id = 2) <> 'USUARIO' THEN
        RAISE EXCEPTION 'O perfil legado deve ser convertido para USUARIO';
    END IF;
    IF (SELECT usuario_perfil FROM usuario WHERE usuario_id = 9) <> 'USUARIO'
       OR EXISTS (SELECT 1 FROM usuario WHERE usuario_senha <> 'hash-ficticio-preservado') THEN
        RAISE EXCEPTION 'SUPORTE deve virar USUARIO e as senhas devem ser preservadas';
    END IF;
    BEGIN
        INSERT INTO usuario (usuario_id, usuario_nome, usuario_email, usuario_perfil)
        VALUES (3, 'ARIEL', 'outro@exemplo.com', 'USUARIO');
        RAISE EXCEPTION 'O nome duplicado deve ser rejeitado';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
    BEGIN
        INSERT INTO usuario (usuario_id, usuario_nome, usuario_email, usuario_perfil)
        VALUES (4, 'outro', ' ARIEL@exemplo.com ', 'USUARIO');
        RAISE EXCEPTION 'O email duplicado deve ser rejeitado';
    EXCEPTION WHEN unique_violation THEN NULL;
    END;
END;
$$;

-- Um administrador ja existente deve ser mantido, sem promover outra conta.
UPDATE usuario SET usuario_perfil = 'USUARIO' WHERE usuario_id = 1;
UPDATE usuario SET usuario_perfil = 'ADMIN' WHERE usuario_id = 2;
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
DO $$
BEGIN
    IF (SELECT count(*) FROM usuario WHERE usuario_perfil = 'ADMIN') <> 1
       OR (SELECT usuario_perfil FROM usuario WHERE usuario_id = 2) <> 'ADMIN' THEN
        RAISE EXCEPTION 'A migracao deve manter o administrador existente';
    END IF;
END;
$$;

CREATE SCHEMA teste_usuarios_duplicados;
SET search_path TO teste_usuarios_duplicados;
CREATE TABLE usuario (usuario_id integer PRIMARY KEY, usuario_nome varchar(255),
                      usuario_email varchar(255), usuario_perfil varchar(255));
INSERT INTO usuario VALUES (1, ' Ariel ', 'um@exemplo.com', NULL),
                           (2, 'ARIEL', 'dois@exemplo.com', NULL);
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF (SELECT count(*) FROM usuario) <> 2
       OR NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_nome = ' Ariel ' AND usuario_perfil IS NULL) THEN
        RAISE EXCEPTION 'Duplicidades devem interromper sem alterar os dados';
    END IF;
END;
$$;

-- Reproduz a duplicidade de e-mails encontrada na demonstracao, com nomes diferentes.
UPDATE usuario SET usuario_nome = 'Colega', usuario_email = ' UM@exemplo.com ' WHERE usuario_id = 2;
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF (SELECT count(*) FROM usuario) <> 2
       OR NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_email = ' UM@exemplo.com ' AND usuario_perfil IS NULL)
       OR EXISTS (SELECT 1 FROM pg_indexes WHERE schemaname = current_schema()
                  AND indexname IN ('ux_usuario_nome_ci', 'ux_usuario_email_ci')) THEN
        RAISE EXCEPTION 'E-mails repetidos devem interromper sem alterar contas ou criar indices';
    END IF;
END;
$$;

-- Nomes e e-mails vazios tambem devem interromper antes de qualquer normalizacao.
UPDATE usuario SET usuario_email = 'dois@example.com', usuario_nome = ' ' WHERE usuario_id = 2;
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_nome = ' ' AND usuario_perfil IS NULL)
       OR NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_nome = ' Ariel ') THEN
        RAISE EXCEPTION 'Nome vazio deve interromper sem alterar os dados';
    END IF;
END;
$$;
UPDATE usuario SET usuario_nome = 'Colega', usuario_email = ' ' WHERE usuario_id = 2;
\set ON_ERROR_STOP off
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
\set ON_ERROR_STOP on
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_email = ' ' AND usuario_perfil IS NULL) THEN
        RAISE EXCEPTION 'Email vazio deve interromper sem alterar os dados';
    END IF;
END;
$$;

-- A correcao do cadastro permite repetir a migracao, preservando as duas contas.
UPDATE usuario SET usuario_email = 'colega@example.com' WHERE usuario_id = 2;
\ir ../../main/resources/db/migration/V002__usuarios_escopo_academico.sql
DO $$
BEGIN
    IF (SELECT count(*) FROM usuario) <> 2
       OR (SELECT count(*) FROM usuario WHERE usuario_perfil = 'ADMIN') <> 1 THEN
        RAISE EXCEPTION 'A migracao deve funcionar depois da correcao sem apagar contas';
    END IF;
END;
$$;
SET search_path TO public;
DROP SCHEMA teste_usuarios_academico CASCADE;
DROP SCHEMA teste_usuarios_duplicados CASCADE;
\echo 'OK: perfis, senhas, normalizacao, unicidade, repeticao, rollback e correcao de duplicidades verificados.'
