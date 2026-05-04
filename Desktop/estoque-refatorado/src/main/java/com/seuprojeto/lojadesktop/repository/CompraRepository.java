package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Integer> {
    List<Compra> findByDataCompraBetween(LocalDate inicio, LocalDate fim);
}
