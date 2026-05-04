-- Garante que a coluna 'localizacao' exista na tabela estoque e seja nullable.
-- Usa PREPARE/EXECUTE para ser idempotente: adiciona se nao existir, corrige se existir.
SET @col_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'estoque'
      AND COLUMN_NAME  = 'localizacao'
);
SET @alter_sql = IF(
    @col_exists = 0,
    'ALTER TABLE estoque ADD COLUMN localizacao VARCHAR(100) NULL DEFAULT NULL',
    'ALTER TABLE estoque MODIFY COLUMN localizacao VARCHAR(100) NULL DEFAULT NULL'
);
PREPARE stmt FROM @alter_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
