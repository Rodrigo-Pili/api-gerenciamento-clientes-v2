package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.AtualizarClienteRequestDTO;
import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.CriadoClienteResponseDTO;
import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.CriarClienteRequestDTO;
import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;
import com.rodrigo.portfolio.gerencimento_clientes.domain.usecases.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@DisplayName("Testes da ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    private Cliente cliente;
    private CriarClienteRequestDTO criarDTO;
    private AtualizarClienteRequestDTO atualizarDTO;

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

        criarDTO = CriarClienteRequestDTO.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .telefone("11999999999")
                .build();

        atualizarDTO = AtualizarClienteRequestDTO.builder()
                .nome("João Silva Santos")
                .email("joao.silva@email.com")
                .telefone("11988888888")
                .build();
    }

    // ==================== TESTES: POST /api/clientes ====================

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void deveCriarClienteComSucesso() throws Exception {
        // Arrange
        when(clienteService.criarCliente(any(Cliente.class))).thenReturn(cliente);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva")))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.email", is("joao@email.com")))
                .andExpect(jsonPath("$.status", is("ATIVO")));

        verify(clienteService, times(1)).criarCliente(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar cliente sem nome")
    void deveRetornar400AoCriarClienteSemNome() throws Exception {
        // Arrange
        criarDTO.setNome(null);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).criarCliente(any());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar cliente com email inválido")
    void deveRetornar400AoCriarClienteComEmailInvalido() throws Exception {
        // Arrange
        criarDTO.setEmail("email-invalido");

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).criarCliente(any());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar cliente com CPF duplicado")
    void deveRetornar400AoCriarClienteComCpfDuplicado() throws Exception {
        // Arrange
        when(clienteService.criarCliente(any(Cliente.class)))
                .thenThrow(new IllegalArgumentException("CPF já cadastrado"));

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(clienteService, times(1)).criarCliente(any(Cliente.class));
    }

    // ==================== TESTES: GET /api/clientes ====================

    @Test
    @DisplayName("Deve listar todos os clientes com sucesso")
    void deveListarTodosClientesComSucesso() throws Exception {
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

        when(clienteService.listarTodos()).thenReturn(List.of(cliente, cliente2));

        // Act & Assert
        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("João Silva")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Maria Santos")));

        verify(clienteService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum cliente existe")
    void deveRetornarListaVaziaQuandoNenhumClienteExiste() throws Exception {
        // Arrange
        when(clienteService.listarTodos()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Deve listar clientes filtrados por status ATIVO")
    void deveListarClientesFiltradosPorStatusAtivo() throws Exception {
        // Arrange
        when(clienteService.listarPorStatus(StatusClienteEnum.ATIVO))
                .thenReturn(List.of(cliente));

        // Act & Assert
        mockMvc.perform(get("/api/clientes")
                        .param("status", "ATIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("ATIVO")));

        verify(clienteService, times(1)).listarPorStatus(StatusClienteEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao filtrar por status INATIVO")
    void deveRetornarListaVaziaAoFiltrarPorStatusInativo() throws Exception {
        // Arrange
        when(clienteService.listarPorStatus(StatusClienteEnum.INATIVO))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/clientes")
                        .param("status", "INATIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clienteService, times(1)).listarPorStatus(StatusClienteEnum.INATIVO);
    }

    // ==================== TESTES: GET /api/clientes/cpf/{cpf} ====================

    @Test
    @DisplayName("Deve buscar cliente por CPF com sucesso")
    void deveBuscarClientePorCpfComSucesso() throws Exception {
        // Arrange
        when(clienteService.buscarPorCpf("12345678901")).thenReturn(cliente);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/cpf/12345678901")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cpf", is("12345678901")))
                .andExpect(jsonPath("$.nome", is("João Silva")));

        verify(clienteService, times(1)).buscarPorCpf("12345678901");
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar cliente por CPF inexistente")
    void deveRetornar404AoBuscarClientePorCpfInexistente() throws Exception {
        // Arrange
        when(clienteService.buscarPorCpf("99999999999"))
                .thenThrow(new RuntimeException("Cliente não encontrado com CPF: 99999999999"));

        // Act & Assert
        mockMvc.perform(get("/api/clientes/cpf/99999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).buscarPorCpf("99999999999");
    }

    // ==================== TESTES: PUT /api/clientes/{id} ====================

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() throws Exception {
        // Arrange
        Cliente clienteAtualizado = Cliente.builder()
                .id(1L)
                .nome("João Silva Santos")
                .cpf("12345678901")
                .email("joao.silva@email.com")
                .telefone("11988888888")
                .status(StatusClienteEnum.ATIVO)
                .dataCadastro(cliente.getDataCadastro())
                .build();

        when(clienteService.atualizarCliente(eq(1L), any(Cliente.class)))
                .thenReturn(clienteAtualizado);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("João Silva Santos")))
                .andExpect(jsonPath("$.email", is("joao.silva@email.com")))
                .andExpect(jsonPath("$.telefone", is("11988888888")));

        verify(clienteService, times(1)).atualizarCliente(eq(1L), any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar cliente inexistente")
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        // Arrange
        when(clienteService.atualizarCliente(eq(999L), any(Cliente.class)))
                .thenThrow(new RuntimeException("Cliente não encontrado com ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarDTO)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).atualizarCliente(eq(999L), any(Cliente.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar cliente com dados inválidos")
    void deveRetornar400AoAtualizarClienteComDadosInvalidos() throws Exception {
        // Arrange
        atualizarDTO.setEmail("email-invalido");

        // Act & Assert
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).atualizarCliente(any(), any());
    }

    // ==================== TESTES: DELETE /api/clientes/{id} ====================

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void deveDeletarClienteComSucesso() throws Exception {
        // Arrange
        doNothing().when(clienteService).deletarCliente(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).deletarCliente(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar cliente inexistente")
    void deveRetornar404AoDeletarClienteInexistente() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Cliente não encontrado com ID: 999"))
                .when(clienteService).deletarCliente(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).deletarCliente(999L);
    }
}