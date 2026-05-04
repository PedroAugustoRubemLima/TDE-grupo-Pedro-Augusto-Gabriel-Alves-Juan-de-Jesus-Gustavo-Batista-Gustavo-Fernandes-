package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByAtivoTrue();
    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
}
