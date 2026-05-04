package com.seuprojeto.lojadesktop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstoque;

    @NotNull(message = "Produto é obrigatório")
    @OneToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull(message = "Quantidade atual é obrigatória")
    @DecimalMin(value = "0.0", message = "Quantidade em estoque não pode ser negativa")
    @Column(nullable = false)
    private Double quantidadeAtual;

    public Estoque() {}

    public Integer getIdEstoque() { return idEstoque; }
    public void setIdEstoque(Integer idEstoque) { this.idEstoque = idEstoque; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Double getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Double quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }
}
