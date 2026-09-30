package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers;

import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.AtualizarClienteRequestDTO;
import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.CriadoClienteResponseDTO;
import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos.CriarClienteRequestDTO;
import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;
import com.rodrigo.portfolio.gerencimento_clientes.domain.usecases.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@Slf4j
@Tag(name = "Clientes", description = "API para gerenciar clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriadoClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF/Email duplicado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CriadoClienteResponseDTO> criarCliente(
            @Valid @RequestBody CriarClienteRequestDTO dto) {
        log.info("POST /api/clientes - Criando novo cliente");
        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .build();

        Cliente clienteCriado = clienteService.criarCliente(cliente);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CriadoClienteResponseDTO.fromEntity(clienteCriado));
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Lista todos os clientes ou filtra por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<CriadoClienteResponseDTO>> listarTodos(
            @Parameter(description = "Status do cliente para filtrar (opcional)")
            @RequestParam(required = false) StatusClienteEnum status) {
        log.info("GET /api/clientes - Listando clientes");
        List<Cliente> clientes;

        if (status != null) {
            clientes = clienteService.listarPorStatus(status);
        } else {
            clientes = clienteService.listarTodos();
        }

        List<CriadoClienteResponseDTO> dtos = clientes.stream()
                .map(CriadoClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Busca um cliente específico pelo seu CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriadoClienteResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CriadoClienteResponseDTO> buscarPorCpf(
            @Parameter(description = "CPF do cliente", example = "12345678901")
            @PathVariable String cpf) {
        log.info("GET /api/clientes/cpf/{} - Buscando cliente por CPF", cpf);
        Cliente cliente = clienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(CriadoClienteResponseDTO.fromEntity(cliente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriadoClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CriadoClienteResponseDTO> atualizarCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AtualizarClienteRequestDTO dto) {
        log.info("PUT /api/clientes/{} - Atualizando cliente", id);
        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .build();

        Cliente clienteAtualizado = clienteService.atualizarCliente(id, cliente);
        return ResponseEntity.ok(CriadoClienteResponseDTO.fromEntity(clienteAtualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente", description = "Remove um cliente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deletarCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Deletando cliente", id);
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}