-- Remove colunas e FK constraints geradas pelo JPA antigo em compras_produtos.
-- Essas colunas (compra_id_compra, produto_id_produto) foram criadas quando o
-- entity usava ddl-auto=update sem @JoinColumn explicito.
-- Usa PREPARE/EXECUTE para ser idempotente (MySQL 8 nao suporta DROP COLUMN IF EXISTS).

-- 1) Remove FK da coluna legada compra_id_compra (se existir)
SET @fk1 = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'compra_id_compra'
      AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);
SET @sql1 = IF(@fk1 IS NULL, 'SELECT 1', CONCAT('ALTER TABLE compras_produtos DROP FOREIGN KEY `', @fk1, '`'));
PREPARE stmt FROM @sql1;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) Remove FK da coluna legada produto_id_produto (se existir)
SET @fk2 = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'produto_id_produto'
      AND REFERENCED_TABLE_NAME IS NOT NULL
    LIMIT 1
);
SET @sql2 = IF(@fk2 IS NULL, 'SELECT 1', CONCAT('ALTER TABLE compras_produtos DROP FOREIGN KEY `', @fk2, '`'));
PREPARE stmt FROM @sql2;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) Remove coluna legada compra_id_compra (se existir)
SET @c1 = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'compra_id_compra'
);
SET @sql3 = IF(@c1 > 0, 'ALTER TABLE compras_produtos DROP COLUMN compra_id_compra', 'SELECT 1');
PREPARE stmt FROM @sql3;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4) Remove coluna legada produto_id_produto (se existir)
SET @c2 = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'produto_id_produto'
);
SET @sql4 = IF(@c2 > 0, 'ALTER TABLE compras_produtos DROP COLUMN produto_id_produto', 'SELECT 1');
PREPARE stmt FROM @sql4;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5) Garante NOT NULL em quantidade (alinhado com o schema original V1)
ALTER TABLE compras_produtos MODIFY COLUMN quantidade DOUBLE NOT NULL;
