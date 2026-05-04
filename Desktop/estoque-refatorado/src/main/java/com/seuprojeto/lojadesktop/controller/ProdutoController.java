package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Produto;
import com.seuprojeto.lojadesktop.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> listar(@RequestParam(required = false) String nome,
                                @RequestParam(required = false) Double estoqueMinimo,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoInicio,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoFim) {
        if (nome != null && !nome.isBlank()) return service.buscarPorNome(nome);
        if (estoqueMinimo != null) return service.listarComEstoqueBaixo(estoqueMinimo);
        if (vencimentoInicio != null && vencimentoFim != null)
            return service.listarPorVencimento(vencimentoInicio, vencimentoFim);
        return service.listarAtivos();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<Produto> salvar(@Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(service.salvar(produto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
