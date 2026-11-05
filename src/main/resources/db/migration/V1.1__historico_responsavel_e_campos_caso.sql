
ALTER TABLE historico_status
    ADD COLUMN responsavel_id BIGINT NULL,
    ADD CONSTRAINT fk_historico_status_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios (id);


ALTER TABLE casos
    ADD COLUMN nome_pessoa      VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN cidade           VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN fonte_autorizacao VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE casos
    ALTER COLUMN nome_pessoa DROP DEFAULT,
    ALTER COLUMN cidade DROP DEFAULT,
    ALTER COLUMN fonte_autorizacao DROP DEFAULT;

CREATE INDEX idx_casos_status ON casos (status);
CREATE INDEX idx_casos_nome_pessoa ON casos (nome_pessoa);
