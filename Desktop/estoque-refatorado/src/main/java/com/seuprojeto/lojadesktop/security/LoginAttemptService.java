package com.seuprojeto.lojadesktop.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proteção contra brute force — espelhado do sistema de controladoria.
 * Janela de 15 min: após 5 falhas, bloqueia por 15 min.
 * Chave: username + IP.
 */
@Service
public class LoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        AttemptInfo info = attempts.get(key.toLowerCase());
        if (info == null) return false;
        if (info.blockedUntil != null && Instant.now().isBefore(info.blockedUntil)) return true;
        if (info.blockedUntil != null && Instant.now().isAfter(info.blockedUntil)) {
            attempts.remove(key.toLowerCase());
        }
        return false;
    }

    public void loginSucceeded(String key) {
        if (key == null) return;
        attempts.remove(key.toLowerCase());
    }

    public void loginFailed(String key) {
        if (key == null) return;
        String normalizedKey = key.toLowerCase();
        AttemptInfo info = attempts.computeIfAbsent(normalizedKey, k -> new AttemptInfo());

        Instant now = Instant.now();
        if (info.firstFailureTime == null || now.isAfter(info.firstFailureTime.plus(WINDOW))) {
            info.firstFailureTime = now;
            info.attempts = 0;
            info.blockedUntil = null;
        }

        info.attempts++;

        if (info.attempts >= MAX_ATTEMPTS && info.blockedUntil == null) {
            info.blockedUntil = now.plus(BLOCK_DURATION);
            logger.warn("Brute force detectado para chave [{}]. Bloqueado até {}", normalizedKey, info.blockedUntil);
        } else {
            logger.info("Login falhou para [{}]. Tentativas: {}", normalizedKey, info.attempts);
        }
    }

    private static class AttemptInfo {
        int attempts = 0;
        Instant firstFailureTime;
        Instant blockedUntil;
    }
}
