package com.seuprojeto.lojadesktop.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário de hash de senha.
 * Usa BCrypt (mesmo padrão da controladoria) — mais seguro que SHA-256 puro,
 * pois inclui salt automático e fator de custo configurável.
 */
public class HashUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashSenha(String senha) {
        return encoder.encode(senha);
    }

    public static boolean verificarSenha(String senhaPlana, String senhaHash) {
        return encoder.matches(senhaPlana, senhaHash);
    }
}
