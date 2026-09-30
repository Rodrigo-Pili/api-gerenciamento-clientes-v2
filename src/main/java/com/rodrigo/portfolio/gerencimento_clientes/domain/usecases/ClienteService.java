package com.rodrigo.portfolio.gerencimento_clientes.domain.usecases;

import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;

import java.util.List;

public interface ClienteService {

    Cliente criarCliente(Cliente cliente);
    Cliente buscarPorCpf(String cpf);
    Cliente buscarPorId(Long id);
    List<Cliente> listarTodos();
    Cliente atualizarCliente(Long id, Cliente cliente);
    void deletarCliente(Long id);
    boolean cpfJaExiste(String cpf);
    boolean emailJaExiste(String email);
    List<Cliente> listarPorStatus(StatusClienteEnum status);
}
