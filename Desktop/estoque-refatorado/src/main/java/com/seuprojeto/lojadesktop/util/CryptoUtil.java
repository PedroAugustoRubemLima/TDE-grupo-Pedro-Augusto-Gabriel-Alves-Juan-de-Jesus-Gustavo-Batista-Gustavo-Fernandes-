package com.seuprojeto.lojadesktop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utilitário de criptografia AES.
 * A chave secreta é lida de application.properties via variável de ambiente AES_SECRET,
 * nunca hardcoded no código-fonte.
 */
@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES";

    private final String secret;

    public CryptoUtil(@Value("${app.crypto.secret}") String secret) {
        if (secret == null || secret.length() != 16) {
            throw new IllegalArgumentException(
                "app.crypto.secret deve ter exatamente 16 caracteres. Valor atual: " +
                (secret == null ? "null" : secret.length() + " caracteres")
            );
        }
        this.secret = secret;
    }

    public String encrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar", e);
        }
    }

    public String decrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(value);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar", e);
        }
    }
}
