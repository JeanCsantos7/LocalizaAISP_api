CREATE TABLE usuarios
(
    id        BIGINT NOT NULL AUTO_INCREMENT,

    nome      VARCHAR(255),
    email     VARCHAR(255),
    senha     VARCHAR(255),
    role      VARCHAR(255),
    criado_em DATETIME(6),
    ativo     BIT(1),
    PRIMARY KEY (id)

);



CREATE TABLE casos
(
    id                   BIGINT NOT NULL AUTO_INCREMENT,
    titulo               VARCHAR(255),
    descricao            VARCHAR(2000),
    regiao               VARCHAR(255),
    faixa_etaria         VARCHAR(255),
    data_desaparecimento DATE,
    status               VARCHAR(255),
    origem               VARCHAR(255),
    observacoes          VARCHAR(2000),
    criado_em            DATETIME(6),
    atualizado_em        DATETIME(6),
    usuario_id           BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_casos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);


CREATE TABLE fotos
(
    id        BINARY(16)   NOT NULL,
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
    status_anterior VARCHAR(255),
    status_atual    VARCHAR(255),
    observacao      VARCHAR(2000),
    data_alteracao  DATETIME(6),
    caso_id         BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_historico_status_caso FOREIGN KEY (caso_id) REFERENCES casos (id)
);

CREATE TABLE solicitacoes
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    tipo          VARCHAR(255),
    motivo        VARCHAR(2000),
    descricao     VARCHAR(2000),
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
    nome       VARCHAR(255),
    email      VARCHAR(255),
    telefone   VARCHAR(255),
    mensagem   VARCHAR(2000),
    criado_em  DATETIME(6),
    caso_id    BIGINT NOT NULL,
    usuario_id BIGINT,
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
        'Jean Carlos Goes',
        'jeanCAdm@localizaisp.com',
        '$2a$12$oeGLWyhj4Y4WDVjxcF9yrOh77s0lUwvI8LNz7thQWvVr7ApDGCGT.',
        'ADMIN',
        '2026-10-29',
        1
    );

