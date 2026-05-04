CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    acao VARCHAR(20) NOT NULL,
    entidade VARCHAR(60) NOT NULL,
    entidade_id VARCHAR(60) NULL,
    usuario VARCHAR(80) NOT NULL,
    detalhe VARCHAR(500) NULL,
    ip VARCHAR(64) NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entidade ON audit_logs (entidade);
CREATE INDEX idx_audit_logs_usuario ON audit_logs (usuario);
CREATE INDEX idx_audit_logs_criado_em ON audit_logs (criado_em);
