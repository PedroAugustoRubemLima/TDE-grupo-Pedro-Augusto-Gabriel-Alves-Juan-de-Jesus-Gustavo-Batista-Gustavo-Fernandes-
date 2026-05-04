package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Estoque;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.repository.EstoqueRepository;
import com.seuprojeto.lojadesktop.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditLogService auditLogService;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutoRepository produtoRepository,
                          AuditLogService auditLogService) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.auditLogService = auditLogService;
    }

    public List<Estoque> listar() {
        return estoqueRepository.findAll();
    }

    @Transactional
    public void retirarEstoque(Integer produtoId, Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para retirada deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Produto não encontrado: " + produtoId));

        Estoque estoque = estoqueRepository.findByProduto(produto)
                .orElseThrow(() -> new java.util.NoSuchElementException("Estoque não encontrado para o produto: " + produtoId));

        if (estoque.getQuantidadeAtual() < quantidade) {
            throw new IllegalArgumentException(
                String.format("Estoque insuficiente. Disponível: %.2f, Solicitado: %.2f",
                        estoque.getQuantidadeAtual(), quantidade)
            );
        }

        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - quantidade);
        estoqueRepository.save(estoque);
        auditLogService.registrar(
                "UPDATE",
                "ESTOQUE",
                estoque.getIdEstoque(),
                "Retirada de " + quantidade + " do produto " + produtoId
        );
    }

    @Transactional
    public void adicionarEstoque(Integer produtoId, Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para adição deve ser maior que zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Produto não encontrado: " + produtoId));

        Estoque estoque = estoqueRepository.findByProduto(produto)
                .orElseGet(() -> {
                    Estoque novo = new Estoque();
                    novo.setProduto(produto);
                    novo.setQuantidadeAtual(0.0);
                    return novo;
                });

        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() + quantidade);
        estoqueRepository.save(estoque);
        auditLogService.registrar(
                "UPDATE",
                "ESTOQUE",
                estoque.getIdEstoque(),
                "Adição de " + quantidade + " ao produto " + produtoId
        );
    }
}
