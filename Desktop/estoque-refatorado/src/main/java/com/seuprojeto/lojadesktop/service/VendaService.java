package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.*;
import com.seuprojeto.lojadesktop.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final EstoqueRepository estoqueRepository;
    private final AuditLogService auditLogService;

    public VendaService(VendaRepository vendaRepository,
                        ItemVendaRepository itemVendaRepository,
                        EstoqueRepository estoqueRepository,
                        AuditLogService auditLogService) {
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.estoqueRepository = estoqueRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Venda registrarVenda(Venda venda) {
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter ao menos um item.");
        }

        venda.setDataVenda(LocalDate.now());
        double total = 0.0;

        // Valida estoque e calcula total ANTES de salvar
        for (ItemVenda item : venda.getItens()) {
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade do item deve ser maior que zero.");
            }
            if (item.getPrecoUnitario() == null || item.getPrecoUnitario() <= 0) {
                throw new IllegalArgumentException("Preço unitário do item deve ser maior que zero.");
            }

            Estoque estoque = estoqueRepository.findByProduto(item.getProduto())
                    .orElseThrow(() -> new java.util.NoSuchElementException(
                            "Estoque não encontrado para o produto: " + item.getProduto().getIdProduto()));

            if (estoque.getQuantidadeAtual() < item.getQuantidade()) {
                throw new IllegalArgumentException(
                    String.format("Estoque insuficiente para '%s'. Disponível: %.2f, Solicitado: %.2f",
                            item.getProduto().getNome(),
                            estoque.getQuantidadeAtual(),
                            item.getQuantidade())
                );
            }

            total += item.getQuantidade() * item.getPrecoUnitario();
        }

        venda.setValorTotal(total);
        Venda vendaSalva = vendaRepository.save(venda);

        // Debita estoque e salva itens
        for (ItemVenda item : venda.getItens()) {
            Estoque estoque = estoqueRepository.findByProduto(item.getProduto()).orElseThrow();
            estoque.setQuantidadeAtual(estoque.getQuantidadeAtual() - item.getQuantidade());
            estoqueRepository.save(estoque);
            item.setVenda(vendaSalva);
        }

        itemVendaRepository.saveAll(venda.getItens());
        auditLogService.registrar(
                "CREATE",
                "VENDA",
                vendaSalva.getIdVenda(),
                "Venda registrada com " + venda.getItens().size() + " itens"
        );
        return vendaSalva;
    }

    public List<Venda> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return vendaRepository.findByDataVendaBetween(inicio, fim);
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }
}
