package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Estoque;
import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.repository.EstoqueRepository;
import com.seuprojeto.lojadesktop.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final EstoqueRepository estoqueRepository;
    private final AuditLogService auditLogService;

    public ProdutoService(ProdutoRepository repository,
                          EstoqueRepository estoqueRepository,
                          AuditLogService auditLogService) {
        this.repository = repository;
        this.estoqueRepository = estoqueRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Produto salvar(Produto produto) {
        produto.setAtivo(true);
        Produto salvo = repository.save(produto);

        // Cria registro de estoque automaticamente se não existir
        if (estoqueRepository.findByProduto(salvo).isEmpty()) {
            Estoque estoque = new Estoque();
            estoque.setProduto(salvo);
            estoque.setQuantidadeAtual(produto.getQuantidade() != null ? produto.getQuantidade() : 0.0);
            estoqueRepository.save(estoque);
        }

        auditLogService.registrar(
                "CREATE",
                "PRODUTO",
                salvo.getIdProduto(),
                "Produto salvo: " + salvo.getNome()
        );

        return salvo;
    }

    public List<Produto> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public List<Produto> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public List<Produto> listarComEstoqueBaixo(Double limite) {
        return repository.findByQuantidadeLessThanAndAtivoTrue(limite);
    }

    public List<Produto> listarPorVencimento(LocalDate inicio, LocalDate fim) {
        return repository.findByDataVencimentoBetweenAndAtivoTrue(inicio, fim);
    }

    public void inativar(Integer id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Produto não encontrado: " + id));
        produto.setAtivo(false);
        repository.save(produto);
        auditLogService.registrar("DELETE", "PRODUTO", id, "Produto inativado");
    }
}
