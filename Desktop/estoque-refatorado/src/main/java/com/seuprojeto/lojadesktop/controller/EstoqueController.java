package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Estoque;
import com.seuprojeto.lojadesktop.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping
    public List<Estoque> listar() {
        return service.listar();
    }

    @PostMapping("/retirar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<Void> retirar(@RequestParam Integer produtoId,
                                        @RequestParam Double quantidade) {
        service.retirarEstoque(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/adicionar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<Void> adicionar(@RequestParam Integer produtoId,
                                          @RequestParam Double quantidade) {
        service.adicionarEstoque(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }
}
