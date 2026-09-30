package com.rodrigo.portfolio.gerencimento_clientes.domain.usecases.impl;

import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;
import com.rodrigo.portfolio.gerencimento_clientes.infra.repositorys.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da ClienteServiceImpl")
public class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .telefone("11999999999")
                .status(StatusClienteEnum.ATIVO)
                .dataCadastro(LocalDateTime.now())
                .build();
    }

    // ==================== TESTES: criarCliente ====================

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void devecriarClienteComSucesso() {
        // Arrange
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        Cliente clienteCriado = clienteService.criarCliente(cliente);

        // Assert
        assertThat(clienteCriado)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("nome", "João Silva")
                .hasFieldOrPropertyWithValue("status", StatusClienteEnum.ATIVO);

        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com CPF duplicado")
    void deveLancarExcecaoAoCriarClienteComCpfDuplicado() {
        // Arrange
        Cliente clienteExistente = Cliente.builder()
                .cpf("12345678901")
                .build();

        when(clienteRepository.findByCpf("12345678901"))
                .thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.criarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CPF já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar cliente com Email duplicado")
    void deveLancarExcecaoAoCriarClienteComEmailDuplicado() {
        // Arrange
        Cliente clienteExistente = Cliente.builder()
                .email("joao@email.com")
                .build();

        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail("joao@email.com"))
                .thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.criarCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email já cadastrado");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve definir status ATIVO ao criar cliente")
    void deveDefinirStatusAtivoAoCriarCliente() {
        // Arrange
        cliente.setStatus(null);
        when(clienteRepository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(clienteRepository.findByEmail(cliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        clienteService.criarCliente(cliente);

        // Assert
        verify(clienteRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusClienteEnum.ATIVO);
    }

    // ==================== TESTES: buscarPorId ====================

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        Cliente clienteEncontrado = clienteService.buscarPorId(1L);

        // Assert
        assertThat(clienteEncontrado)
                .isNotNull()
                .isEqualTo(cliente);

        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por ID inexistente")
    void deveLancarExcecaoAoBuscarClientePorIdInexistente() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado com ID: 999");
    }

    // ==================== TESTES: buscarPorCpf ====================

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpfComSucesso() {
        // Arrange
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));

        // Act
        Cliente clienteEncontrado = clienteService.buscarPorCpf("12345678901");

        // Assert
        assertThat(clienteEncontrado)
                .isNotNull()
                .hasFieldOrPropertyWithValue("cpf", "12345678901");

        verify(clienteRepository, times(1)).findByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente por CPF inexistente")
    void deveLancarExcecaoAoBuscarClientePorCpfInexistente() {
        // Arrange
        when(clienteRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.buscarPorCpf("99999999999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado com CPF: 99999999999");
    }

    // ==================== TESTES: atualizarCliente ====================

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        Cliente clienteAtualizado = Cliente.builder()
                .nome("João Silva Santos")
                .email("joao.silva@email.com")
                .telefone("11988888888")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        clienteService.atualizarCliente(1L, clienteAtualizado);

        // Assert
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));

        assertThat(cliente.getNome()).isEqualTo("João Silva Santos");
        assertThat(cliente.getEmail()).isEqualTo("joao.silva@email.com");
        assertThat(cliente.getTelefone()).isEqualTo("11988888888");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.atualizarCliente(999L, cliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado com ID: 999");

        verify(clienteRepository, never()).save(any());
    }

    // ==================== TESTES: deletarCliente ====================

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // Act
        clienteService.deletarCliente(1L);

        // Assert
        verify(clienteRepository, times(1)).existsById(1L);
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        // Arrange
        when(clienteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> clienteService.deletarCliente(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado com ID: 999");

        verify(clienteRepository, never()).deleteById(any());
    }

    // ==================== TESTES: cpfJaExiste ====================

    @Test
    @DisplayName("Deve retornar true quando CPF já existe")
    void deveRetornarTrueQuandoCpfJaExiste() {
        // Arrange
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));

        // Act
        boolean existe = clienteService.cpfJaExiste("12345678901");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando CPF não existe")
    void deveRetornarFalseQuandoCpfNaoExiste() {
        // Arrange
        when(clienteRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        // Act
        boolean existe = clienteService.cpfJaExiste("99999999999");

        // Assert
        assertThat(existe).isFalse();
    }

    // ==================== TESTES: emailJaExiste ====================

    @Test
    @DisplayName("Deve retornar true quando Email já existe")
    void deveRetornarTrueQuandoEmailJaExiste() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(cliente));

        // Act
        boolean existe = clienteService.emailJaExiste("joao@email.com");

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando Email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        // Arrange
        when(clienteRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        // Act
        boolean existe = clienteService.emailJaExiste("naoexiste@email.com");

        // Assert
        assertThat(existe).isFalse();
    }

    // ==================== TESTES: listarPorStatus ====================

    @Test
    @DisplayName("Deve listar clientes por status com sucesso")
    void deveListarClientesPorStatusComSucesso() {
        // Arrange
        List<Cliente> clientes = List.of(cliente);
        when(clienteRepository.findByStatus(StatusClienteEnum.ATIVO))
                .thenReturn(clientes);

        // Act
        List<Cliente> resultado = clienteService.listarPorStatus(StatusClienteEnum.ATIVO);

        // Assert
        assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(cliente);

        verify(clienteRepository, times(1)).findByStatus(StatusClienteEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum cliente encontrado")
    void deveRetornarListaVaziaQuandoNenhumClienteEncontrado() {
        // Arrange
        when(clienteRepository.findByStatus(StatusClienteEnum.INATIVO))
                .thenReturn(List.of());

        // Act
        List<Cliente> resultado = clienteService.listarPorStatus(StatusClienteEnum.INATIVO);

        // Assert
        assertThat(resultado).isEmpty();
    }

    // ==================== TESTES: listarTodos ====================

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void deveListarTodosClientesComSucesso() {
        // Arrange
        Cliente cliente2 = Cliente.builder()
                .id(2L)
                .nome("Maria Santos")
                .cpf("98765432101")
                .email("maria@email.com")
                .telefone("11988888888")
                .status(StatusClienteEnum.ATIVO)
                .dataCadastro(LocalDateTime.now())
                .build();

        List<Cliente> clientes = List.of(cliente, cliente2);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Act
        List<Cliente> resultado = clienteService.listarTodos();

        // Assert
        assertThat(resultado)
                .isNotEmpty()
                .hasSize(2)
                .contains(cliente, cliente2);

        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum cliente existe")
    void deveRetornarListaVaziaQuandoNenhumClienteExiste() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(List.of());

        // Act
        List<Cliente> resultado = clienteService.listarTodos();

        // Assert
        assertThat(resultado).isEmpty();

        verify(clienteRepository, times(1)).findAll();
    }
}
