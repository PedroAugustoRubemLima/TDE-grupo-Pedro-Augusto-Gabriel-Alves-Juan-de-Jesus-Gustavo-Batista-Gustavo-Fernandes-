package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public FuncionarioService(FuncionarioRepository repository,
                              PasswordEncoder passwordEncoder,
                              AuditLogService auditLogService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public Funcionario salvar(Funcionario funcionario) {
        if (repository.existsByUsuario(funcionario.getUsuario()) &&
                (funcionario.getIdFuncionario() == null ||
                 !repository.findByUsuario(funcionario.getUsuario())
                         .map(f -> f.getIdFuncionario().equals(funcionario.getIdFuncionario()))
                         .orElse(false))) {
            throw new IllegalArgumentException("Usuário '" + funcionario.getUsuario() + "' já está em uso.");
        }
        // Só faz hash se a senha não parece já estar hasheada (começa com $2a$)
        String senha = funcionario.getSenha();
        if (senha != null && !senha.startsWith("$2a$")) {
            funcionario.setSenha(passwordEncoder.encode(senha));
        }
        funcionario.setAtivo(true);
        Funcionario salvo = repository.save(funcionario);
        auditLogService.registrar(
                "CREATE",
                "FUNCIONARIO",
                salvo.getIdFuncionario(),
                "Funcionário salvo: " + salvo.getUsuario()
        );
        return salvo;
    }

    public List<Funcionario> listarAtivos() {
        return repository.findAll()
                .stream()
                .filter(Funcionario::getAtivo)
                .toList();
    }

    /**
     * Usado pelo JwtRequestFilter para carregar o funcionário pelo username do token.
     */
    public Optional<Funcionario> buscarPorUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }

    /**
     * Verifica credenciais — usado no login.
     */
    public Optional<Funcionario> autenticar(String usuario, String senha) {
        Optional<Funcionario> funcionarioOpt = repository.findByUsuarioAndAtivoTrue(usuario);
        if (funcionarioOpt.isPresent()) {
            Funcionario f = funcionarioOpt.get();
            if (passwordEncoder.matches(senha, f.getSenha())) {
                return funcionarioOpt;
            }
        }
        return Optional.empty();
    }

    public void inativar(Integer id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Funcionário não encontrado: " + id));
        funcionario.setAtivo(false);
        repository.save(funcionario);
        auditLogService.registrar("DELETE", "FUNCIONARIO", id, "Funcionário inativado");
    }
}
