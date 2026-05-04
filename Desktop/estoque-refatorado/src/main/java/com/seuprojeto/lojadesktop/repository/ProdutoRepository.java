package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByAtivoTrue();
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    List<Produto> findByQuantidadeLessThanAndAtivoTrue(Double quantidade);
    List<Produto> findByDataVencimentoBetweenAndAtivoTrue(LocalDate inicio, LocalDate fim);
}
