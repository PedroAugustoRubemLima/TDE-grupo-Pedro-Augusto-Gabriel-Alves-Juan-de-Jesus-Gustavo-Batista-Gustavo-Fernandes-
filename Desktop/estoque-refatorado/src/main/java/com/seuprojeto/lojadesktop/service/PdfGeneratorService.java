package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Venda;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] gerarRelatorioVendas(List<Venda> vendas) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // PDFBox 3.x: fontes padrão agora usam Standard14Fonts.FontName
        PDType1Font fontBold   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            content.setFont(fontBold, 14);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Relatorio de Vendas");
            content.newLineAtOffset(0, -25);

            content.setFont(fontNormal, 11);
            double totalGeral = 0.0;

            for (Venda venda : vendas) {
                String linha = String.format("Venda #%d  |  Data: %s  |  Total: R$ %.2f  |  Cliente: %s",
                        venda.getIdVenda(),
                        venda.getDataVenda(),
                        venda.getValorTotal() != null ? venda.getValorTotal() : 0.0,
                        venda.getCliente() != null ? venda.getCliente().getNome() : "-"
                );
                content.showText(linha);
                content.newLineAtOffset(0, -18);
                totalGeral += venda.getValorTotal() != null ? venda.getValorTotal() : 0.0;
            }

            content.newLineAtOffset(0, -10);
            content.setFont(fontBold, 12);
            content.showText(String.format("TOTAL GERAL: R$ %.2f", totalGeral));
            content.endText();
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        document.save(output);
        document.close();
        return output.toByteArray();
    }
}
