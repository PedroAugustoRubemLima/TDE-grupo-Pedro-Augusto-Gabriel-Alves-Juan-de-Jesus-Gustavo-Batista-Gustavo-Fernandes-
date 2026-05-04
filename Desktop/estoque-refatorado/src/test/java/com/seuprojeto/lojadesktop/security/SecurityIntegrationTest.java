package com.seuprojeto.lojadesktop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.service.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private FuncionarioService funcionarioService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setup() {
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(funcionarioService.autenticar(anyString(), anyString())).thenReturn(Optional.empty());
    }

    @Test
    void deveBloquearPostSemCsrfMesmoComJwtValido() throws Exception {
        mockarAutenticacaoJwt("token-admin", "ADMIN");

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nome", "Queijo Teste",
                                "tipo", "Laticinio",
                                "preco", 12.5,
                                "quantidade", 10.0
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAplicarRateLimitNoEndpointDeLogin() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of("usuario", "admin", "senha", "x"));
        for (int i = 0; i < 15; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void deveRejeitarTokenExpiradoNoAcessoApi() throws Exception {
        when(jwtUtil.extractUsername("token-expirado"))
                .thenThrow(new RuntimeException("JWT expired"));

        mockMvc.perform(get("/api/produtos")
                        .header("Authorization", "Bearer token-expirado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarExclusaoFuncionarioParaPerfilNaoAdmin() throws Exception {
        mockarAutenticacaoJwt("token-vendedor", "VENDEDOR");

        mockMvc.perform(delete("/api/funcionarios/1")
                        .header("Authorization", "Bearer token-vendedor")
                        .cookie(new MockCookie("XSRF-TOKEN", "abc123"))
                        .header("X-XSRF-TOKEN", "abc123"))
                .andExpect(status().isForbidden());
    }

    private void mockarAutenticacaoJwt(String token, String cargo) {
        Funcionario funcionario = new Funcionario();
        funcionario.setUsuario("user-" + cargo.toLowerCase());
        funcionario.setCargo(cargo);
        funcionario.setAtivo(true);

        when(jwtUtil.extractUsername(token)).thenReturn(funcionario.getUsuario());
        when(funcionarioService.buscarPorUsuario(funcionario.getUsuario())).thenReturn(Optional.of(funcionario));
        when(jwtUtil.validateToken(token, funcionario)).thenReturn(true);
    }
}
