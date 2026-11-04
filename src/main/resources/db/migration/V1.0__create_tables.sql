CREATE TABLE usuarios
(
    id        BIGINT NOT NULL AUTO_INCREMENT,

    nome      VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL  UNIQUE  ,
    senha     VARCHAR(255) NOT NULL,
    role      VARCHAR(255),
    criado_em DATETIME(6),
    ativo     BIT(1),
    PRIMARY KEY (id)

);



CREATE TABLE casos
(
    id                   BIGINT NOT NULL AUTO_INCREMENT,
    titulo               VARCHAR(255) NOT NULL,
    descricao            VARCHAR(2000) NOT NULL,
    regiao               VARCHAR(255) NOT NULL,
    faixa_etaria         VARCHAR(255) NOT NULL,
    data_desaparecimento DATE NOT NULL,
    status               VARCHAR(255),
    origem               VARCHAR(255) NOT NULL,
    observacoes          VARCHAR(2000),
    criado_em            DATETIME(6),
    atualizado_em        DATETIME(6),
    usuario_id           BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_casos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);


CREATE TABLE fotos
(
    id        BIGINT   NOT NULL,
    url       VARCHAR(255),
    legenda   VARCHAR(255),
    principal BIT(1),
    criado_em DATETIME(6),
    caso_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_fotos_caso FOREIGN KEY (caso_id) REFERENCES casos (id)
);

CREATE TABLE historico_status
(
    id              BIGINT NOT NULL AUTO_INCREMENT,
    status_anterior VARCHAR(255) NOT NULL,
    status_atual    VARCHAR(255) NOT NULL,
    observacao      VARCHAR(2000),
    data_alteracao  DATETIME(6),
    caso_id         BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_historico_status_caso FOREIGN KEY (caso_id) REFERENCES casos (id)
);

CREATE TABLE solicitacoes
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    tipo          VARCHAR(255) NOT NULL,
    motivo        VARCHAR(2000) NOT NULL,
    descricao     VARCHAR(2000) NOT NULL,
    status        VARCHAR(255),
    criado_em     DATETIME(6),
    respondido_em DATETIME(6),
    usuario_id    BIGINT NOT NULL,
    caso_id       BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_solicitacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_solicitacoes_caso FOREIGN KEY (caso_id) REFERENCES casos (id)
);


CREATE TABLE informacoes_contato
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    telefone   VARCHAR(255) NOT NULL,
    mensagem   VARCHAR(2000) NOT NULL,
    criado_em  DATETIME(6),
    caso_id    BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_informacoes_contato_caso FOREIGN KEY (caso_id) REFERENCES casos (id),
    CONSTRAINT fk_informacoes_contato_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

INSERT INTO usuarios
(
    nome,
    email,
    senha,
    role,
   criado_em,
   ativo

)
VALUES
    (
        '${SEED_NAME}',
        '${SEED_EMAIL}',
        '${SEED_SENHA}',
        'ADMIN',
        '2026-10-29',
        1
    );

