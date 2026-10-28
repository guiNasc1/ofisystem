-- Aplicar antes de executar a versao com os perfis ADMIN e USUARIO.
-- Nenhum usuario e apagado. Duplicidades devem ser revisadas manualmente.
BEGIN;
SET LOCAL lock_timeout = '10s';
LOCK TABLE usuario IN ACCESS EXCLUSIVE MODE;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM usuario
               WHERE usuario_nome IS NULL OR btrim(usuario_nome) = ''
                  OR usuario_email IS NULL OR btrim(usuario_email) = '') THEN
        RAISE EXCEPTION 'Existem usuarios sem nome ou e-mail. Revise os cadastros antes da migracao.';
    END IF;
    IF EXISTS (SELECT lower(btrim(usuario_nome)) FROM usuario
               GROUP BY lower(btrim(usuario_nome)) HAVING count(*) > 1) THEN
        RAISE EXCEPTION 'Existem nomes de usuario repetidos, ignorando maiusculas e espacos nas pontas. Nenhum dado foi alterado.';
    END IF;
    IF EXISTS (SELECT lower(btrim(usuario_email)) FROM usuario
               GROUP BY lower(btrim(usuario_email)) HAVING count(*) > 1) THEN
        RAISE EXCEPTION 'Existem e-mails repetidos. Nenhum dado foi alterado.';
    END IF;
END;
$$;

UPDATE usuario SET usuario_nome = lower(btrim(usuario_nome)),
                   usuario_email = lower(btrim(usuario_email));
UPDATE usuario SET usuario_perfil = 'USUARIO'
WHERE usuario_perfil IS NULL OR usuario_perfil IN ('GERENTE', 'SUPORTE');

-- Uma base antiga sem administrador passa a usar a conta mais antiga para a demonstracao.
UPDATE usuario SET usuario_perfil = 'ADMIN'
WHERE usuario_id = (SELECT min(usuario_id) FROM usuario)
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE usuario_perfil = 'ADMIN');

ALTER TABLE usuario ALTER COLUMN usuario_perfil SET DEFAULT 'USUARIO';
ALTER TABLE usuario ALTER COLUMN usuario_perfil SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_nome_ci ON usuario (lower(btrim(usuario_nome)));
CREATE UNIQUE INDEX IF NOT EXISTS ux_usuario_email_ci ON usuario (lower(btrim(usuario_email)));
COMMIT;
