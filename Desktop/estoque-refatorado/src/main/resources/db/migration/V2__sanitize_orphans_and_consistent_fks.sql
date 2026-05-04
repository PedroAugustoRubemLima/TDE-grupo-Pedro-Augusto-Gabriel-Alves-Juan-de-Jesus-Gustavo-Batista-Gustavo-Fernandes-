DELETE iv
FROM itens_venda iv
LEFT JOIN produtos p ON p.id_produto = iv.produto_id
WHERE p.id_produto IS NULL;

DELETE iv
FROM itens_venda iv
LEFT JOIN vendas v ON v.id_venda = iv.venda_id
WHERE iv.venda_id IS NOT NULL
  AND v.id_venda IS NULL;

DELETE cp
FROM compras_produtos cp
LEFT JOIN produtos p ON p.id_produto = cp.produto_id
WHERE p.id_produto IS NULL;

DELETE cp
FROM compras_produtos cp
LEFT JOIN compras c ON c.id_compra = cp.compra_id
WHERE cp.compra_id IS NOT NULL
  AND c.id_compra IS NULL;

DELETE e
FROM estoque e
LEFT JOIN produtos p ON p.id_produto = e.produto_id
WHERE p.id_produto IS NULL;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'itens_venda'
      AND COLUMN_NAME = 'produto_id'
      AND REFERENCED_TABLE_NAME = 'produtos'
    LIMIT 1
);
SET @sql = IF(@fk_name IS NULL, 'SELECT 1', CONCAT('ALTER TABLE itens_venda DROP FOREIGN KEY ', @fk_name));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'itens_venda'
      AND COLUMN_NAME = 'venda_id'
      AND REFERENCED_TABLE_NAME = 'vendas'
    LIMIT 1
);
SET @sql = IF(@fk_name IS NULL, 'SELECT 1', CONCAT('ALTER TABLE itens_venda DROP FOREIGN KEY ', @fk_name));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'estoque'
      AND COLUMN_NAME = 'produto_id'
      AND REFERENCED_TABLE_NAME = 'produtos'
    LIMIT 1
);
SET @sql = IF(@fk_name IS NULL, 'SELECT 1', CONCAT('ALTER TABLE estoque DROP FOREIGN KEY ', @fk_name));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'produto_id'
      AND REFERENCED_TABLE_NAME = 'produtos'
    LIMIT 1
);
SET @sql = IF(@fk_name IS NULL, 'SELECT 1', CONCAT('ALTER TABLE compras_produtos DROP FOREIGN KEY ', @fk_name));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'compras_produtos'
      AND COLUMN_NAME = 'compra_id'
      AND REFERENCED_TABLE_NAME = 'compras'
    LIMIT 1
);
SET @sql = IF(@fk_name IS NULL, 'SELECT 1', CONCAT('ALTER TABLE compras_produtos DROP FOREIGN KEY ', @fk_name));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE estoque
    ADD CONSTRAINT fk_estoque_produto
        FOREIGN KEY (produto_id) REFERENCES produtos(id_produto)
        ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE itens_venda
    ADD CONSTRAINT fk_itens_venda_produto
        FOREIGN KEY (produto_id) REFERENCES produtos(id_produto)
        ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE itens_venda
    ADD CONSTRAINT fk_itens_venda_venda
        FOREIGN KEY (venda_id) REFERENCES vendas(id_venda)
        ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE vendas
    ADD CONSTRAINT fk_vendas_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE vendas
    ADD CONSTRAINT fk_vendas_funcionario
        FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id_funcionario)
        ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE compras_produtos
    ADD CONSTRAINT fk_compras_produtos_compra
        FOREIGN KEY (compra_id) REFERENCES compras(id_compra)
        ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE compras_produtos
    ADD CONSTRAINT fk_compras_produtos_produto
        FOREIGN KEY (produto_id) REFERENCES produtos(id_produto)
        ON UPDATE CASCADE ON DELETE RESTRICT;
