package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.ComPro;
import com.seuprojeto.lojadesktop.model.Compra;
import com.seuprojeto.lojadesktop.model.Estoque;
import com.seuprojeto.lojadesktop.repository.ComProRepository;
import com.seuprojeto.lojadesktop.repository.CompraRepository;
import com.seuprojeto.lojadesktop.repository.EstoqueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ComProRepository comProRepository;
    private final EstoqueRepository estoqueRepository;

    public CompraService(CompraRepository compraRepository,
                         ComProRepository comProRepository,
                         EstoqueRepository estoqueRepository) {
        this.compraRepository = compraRepository;
        this.comProRepository = comProRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional
    public Compra registrarCompra(Compra compra) {
        if (compra.getItens() == null || compra.getItens().isEmpty()) {
            throw new IllegalArgumentException("A compra deve conter ao menos um item.");
        }

        compra.setDataCompra(LocalDate.now());

        // Salva a compra primeiro para gerar o ID
        Compra salva = compraRepository.save(compra);

        // Associa os itens à compra salva e atualiza o estoque
        for (ComPro item : compra.getItens()) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade do item de compra deve ser maior que zero.");
            }

            item.setCompra(salva);
            comProRepository.save(item);

            // Adiciona ao estoque
            Estoque estoque = estoqueRepository.findByProduto(item.getProduto())
                    .orElseGet(() -> {
                        Estoque novo = new Estoque();
                        novo.setProduto(item.getProduto());
                        novo.setQuantidadeAtual(0.0);
                        return novo;
                    });

            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() + item.getQuantidade());
            estoqueRepository.save(estoque);
        }

        return salva;
    }

    public List<Compra> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return compraRepository.findByDataCompraBetween(inicio, fim);
    }

    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }
}
