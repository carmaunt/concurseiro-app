CREATE TABLE comentarios (
    id BIGSERIAL PRIMARY KEY,
    questao_id VARCHAR(255) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    texto TEXT NOT NULL,
    curtidas INTEGER NOT NULL DEFAULT 0,
    descurtidas INTEGER NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_comentarios_questao_id ON comentarios(questao_id);
CREATE INDEX idx_comentarios_curtidas ON comentarios(questao_id, curtidas DESC);
CREATE INDEX idx_comentarios_criado_em ON comentarios(questao_id, criado_em DESC);
