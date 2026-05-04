package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Integer> {
    List<Venda> findByDataVendaBetween(LocalDate inicio, LocalDate fim);
}
