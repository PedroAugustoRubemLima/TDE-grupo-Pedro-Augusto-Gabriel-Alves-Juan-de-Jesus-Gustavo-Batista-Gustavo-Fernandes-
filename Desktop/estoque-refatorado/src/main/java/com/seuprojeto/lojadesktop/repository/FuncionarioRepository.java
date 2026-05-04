package com.seuprojeto.lojadesktop.repository;

import com.seuprojeto.lojadesktop.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    Optional<Funcionario> findByUsuarioAndAtivoTrue(String usuario);
    Optional<Funcionario> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}
