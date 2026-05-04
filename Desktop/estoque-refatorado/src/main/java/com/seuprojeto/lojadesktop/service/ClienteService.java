package com.seuprojeto.lojadesktop.service;

import com.seuprojeto.lojadesktop.model.Cliente;
import com.seuprojeto.lojadesktop.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AuditLogService auditLogService;

    public ClienteService(ClienteRepository repository,
                          AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    public Cliente salvar(Cliente cliente) {
        cliente.setAtivo(true);
        Cliente salvo = repository.save(cliente);
        auditLogService.registrar("CREATE", "CLIENTE", salvo.getIdCliente(), "Cliente salvo: " + salvo.getNome());
        return salvo;
    }

    public List<Cliente> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public void inativar(Integer id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Cliente não encontrado: " + id));
        cliente.setAtivo(false);
        repository.save(cliente);
        auditLogService.registrar("DELETE", "CLIENTE", id, "Cliente inativado");
    }
}
