CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14),
    telefone VARCHAR(20),
    email VARCHAR(100),
    ativo BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS funcionarios (
    id_funcionario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14),
    cargo VARCHAR(80) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME NULL,
    updated_at DATETIME NULL
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(60) NOT NULL,
    preco DOUBLE NOT NULL,
    quantidade DOUBLE NOT NULL,
    data_vencimento DATE NULL,
    image_path VARCHAR(255) NULL,
    ativo BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS estoque (
    id_estoque INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL UNIQUE,
    quantidade_atual DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS vendas (
    id_venda INT AUTO_INCREMENT PRIMARY KEY,
    data_venda DATE NULL,
    valor_total DOUBLE NULL,
    cliente_id INT NULL,
    funcionario_id INT NULL
);

CREATE TABLE IF NOT EXISTS itens_venda (
    id_item_venda INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    quantidade DOUBLE NOT NULL,
    preco_unitario DOUBLE NOT NULL,
    venda_id INT NULL
);

CREATE TABLE IF NOT EXISTS compras (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    data_compra DATE NULL,
    valor_total DOUBLE NULL
);

CREATE TABLE IF NOT EXISTS compras_produtos (
    id_com_pro INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NULL,
    produto_id INT NOT NULL,
    quantidade DOUBLE NOT NULL
);
