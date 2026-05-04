package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.AuditLog;
import com.seuprojeto.lojadesktop.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void registrar(String acao, String entidade, Object entidadeId, String detalhe) {
        AuditLog log = new AuditLog();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId == null ? null : String.valueOf(entidadeId));
        log.setUsuario(getUsuarioAtual());
        log.setIp(getClientIp());
        log.setDetalhe(detalhe);
        auditLogRepository.save(log);
    }

    private String getUsuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "sistema";
        }
        return authentication.getName();
    }

    private String getClientIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
