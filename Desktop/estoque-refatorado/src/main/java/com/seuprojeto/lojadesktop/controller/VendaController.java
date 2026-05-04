package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Venda;
import com.seuprojeto.lojadesktop.service.PdfGeneratorService;
import com.seuprojeto.lojadesktop.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;
    private final PdfGeneratorService pdfService;

    public VendaController(VendaService vendaService, PdfGeneratorService pdfService) {
        this.vendaService = vendaService;
        this.pdfService = pdfService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','VENDEDOR')")
    public ResponseEntity<Venda> registrar(@Valid @RequestBody Venda venda) {
        return ResponseEntity.ok(vendaService.registrarVenda(venda));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public List<Venda> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public List<Venda> porPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return vendaService.buscarPorPeriodo(inicio, fim);
    }

    @GetMapping("/relatorio/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<byte[]> gerarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) throws IOException {

        List<Venda> vendas = (inicio != null && fim != null)
                ? vendaService.buscarPorPeriodo(inicio, fim)
                : vendaService.listarTodas();

        byte[] pdf = pdfService.gerarRelatorioVendas(vendas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-vendas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
