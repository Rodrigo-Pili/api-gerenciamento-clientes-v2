package com.rodrigo.portfolio.gerencimento_clientes.domain.usecases.impl;

import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;
import com.rodrigo.portfolio.gerencimento_clientes.domain.usecases.ClienteService;
import com.rodrigo.portfolio.gerencimento_clientes.infra.repositorys.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;

    @Override
    public Cliente criarCliente(Cliente cliente) {
        if (cpfJaExiste(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (emailJaExiste(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        cliente.setStatus(StatusClienteEnum.ATIVO);
        log.info("Criando cliente: {}", cliente.getNome());
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com CPF: " + cpf));
    }

    @Override
    @Transactional
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        log.info("Atualizando cliente: {}", id);
        return clienteRepository.save(clienteExistente);
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional
    public void deletarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }
        log.info("Deletando cliente: {}", id);
        clienteRepository.deleteById(id);
    }

    @Override
    public boolean cpfJaExiste(String cpf) {
        return clienteRepository.findByCpf(cpf).isPresent();
    }

    @Override
    public boolean emailJaExiste(String email) {
        return clienteRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<Cliente> listarPorStatus(StatusClienteEnum status) {
        return clienteRepository.findByStatus(status);
    }
}
