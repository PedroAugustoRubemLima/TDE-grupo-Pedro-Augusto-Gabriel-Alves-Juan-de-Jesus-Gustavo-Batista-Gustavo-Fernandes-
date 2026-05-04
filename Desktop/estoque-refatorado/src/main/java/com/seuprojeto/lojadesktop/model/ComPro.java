package com.seuprojeto.lojadesktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "compras_produtos")
public class ComPro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComPro;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @NotNull(message = "Produto é obrigatório")
    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.01", message = "Quantidade deve ser maior que zero")
    private Double quantidade;

    public ComPro() {}

    public Integer getIdComPro() { return idComPro; }
    public void setIdComPro(Integer idComPro) { this.idComPro = idComPro; }
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
}
