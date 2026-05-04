package com.seuprojeto.lojadesktop.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagens")
public class ImageController {

    @GetMapping
    public List<String> listarImagens() {
        return List.of(
            "/assets/product_images/queijo1.png",
            "/assets/product_images/queijo mussarela.jpg"
        );
    }
}
