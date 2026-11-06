-- RN07 / RF09: registrar quem respondeu a solicitacao e a justificativa da decisao
ALTER TABLE solicitacoes
    ADD COLUMN resposta       VARCHAR(2000) NULL,
    ADD COLUMN responsavel_id BIGINT NULL,
    ADD CONSTRAINT fk_solicitacoes_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios (id);

-- fila de moderacao filtra por status
CREATE INDEX idx_solicitacoes_status ON solicitacoes (status);
