package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Estoque;
import com.seuprojeto.lojadesktop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {
    Optional<Estoque> findByProduto(Produto produto);
}
