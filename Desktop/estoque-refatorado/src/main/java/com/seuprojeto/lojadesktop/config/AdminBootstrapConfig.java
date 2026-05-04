package com.seuprojeto.lojadesktop.config;

import com.seuprojeto.lojadesktop.model.Funcionario;
import com.seuprojeto.lojadesktop.repository.FuncionarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    @Bean
    public CommandLineRunner bootstrapAdmin(FuncionarioRepository funcionarioRepository,
                                            PasswordEncoder passwordEncoder,
                                            @Value("${app.bootstrap.admin.enabled:true}") boolean enabled,
                                            @Value("${app.bootstrap.admin.usuario:admin}") String usuario,
                                            @Value("${app.bootstrap.admin.senha:admin123}") String senha,
                                            @Value("${app.bootstrap.admin.nome:Administrador}") String nome,
                                            @Value("${app.bootstrap.admin.cargo:ADMIN}") String cargo) {
        return args -> {
            if (!enabled) {
                return;
            }

            if (funcionarioRepository.existsByUsuario(usuario)) {
                logger.info("Bootstrap admin ignorado: usuário '{}' já existe.", usuario);
                return;
            }

            Funcionario admin = new Funcionario();
            admin.setNome(nome);
            admin.setCargo(cargo);
            admin.setUsuario(usuario);
            admin.setSenha(passwordEncoder.encode(senha));
            admin.setAtivo(true);

            funcionarioRepository.save(admin);
            logger.warn("Usuário admin bootstrap criado: usuario='{}'. Altere a senha após o primeiro login.", usuario);
        };
    }
}
