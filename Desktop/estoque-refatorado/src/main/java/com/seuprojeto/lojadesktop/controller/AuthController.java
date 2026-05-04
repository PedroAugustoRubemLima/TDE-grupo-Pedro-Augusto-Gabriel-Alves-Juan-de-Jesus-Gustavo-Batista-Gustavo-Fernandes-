package com.seuprojeto.lojadesktop.controller;

import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.security.JwtUtil;
import com.seuprojeto.lojadesktop.security.LoginAttemptService;
import com.seuprojeto.lojadesktop.service.FuncionarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Autenticação JWT — espelhado do sistema de controladoria.
 * Proteção contra brute force via LoginAttemptService.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FuncionarioService funcionarioService;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(FuncionarioService funcionarioService,
                          JwtUtil jwtUtil,
                          LoginAttemptService loginAttemptService) {
        this.funcionarioService = funcionarioService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        String key = request.getUsuario().toLowerCase() + "|" + clientIp;

        // Bloqueia se excedeu tentativas
        if (loginAttemptService.isBlocked(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Credenciais inválidas"));
        }

        Optional<Funcionario> funcionarioOpt = funcionarioService.autenticar(
                request.getUsuario(), request.getSenha());

        if (funcionarioOpt.isEmpty()) {
            loginAttemptService.loginFailed(key);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Credenciais inválidas"));
        }

        Funcionario funcionario = funcionarioOpt.get();
        String token = jwtUtil.generateToken(funcionario);
        loginAttemptService.loginSucceeded(key);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("funcionario", new FuncionarioResponse(funcionario));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            Optional<Funcionario> func = funcionarioService.buscarPorUsuario(username);
            if (func.isPresent() && jwtUtil.validateToken(token, func.get())) {
                return ResponseEntity.ok(new FuncionarioResponse(func.get()));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Token inválido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Token inválido"));
        }
    }

    // DTO de request
    public static class LoginRequest {
        @NotBlank(message = "Usuário é obrigatório")
        private String usuario;

        @NotBlank(message = "Senha é obrigatória")
        private String senha;

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }

    // DTO de resposta (nunca expõe senha)
    public static class FuncionarioResponse {
        private Integer id;
        private String nome;
        private String usuario;
        private String cargo;

        public FuncionarioResponse(Funcionario f) {
            this.id = f.getIdFuncionario();
            this.nome = f.getNome();
            this.usuario = f.getUsuario();
            this.cargo = f.getCargo();
        }

        public Integer getId() { return id; }
        public String getNome() { return nome; }
        public String getUsuario() { return usuario; }
        public String getCargo() { return cargo; }
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
