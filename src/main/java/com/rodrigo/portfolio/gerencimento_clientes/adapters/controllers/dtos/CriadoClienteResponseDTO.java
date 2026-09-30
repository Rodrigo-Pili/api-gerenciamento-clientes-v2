package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos;

import com.rodrigo.portfolio.gerencimento_clientes.domain.entities.Cliente;
import com.rodrigo.portfolio.gerencimento_clientes.domain.enums.StatusClienteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de resposta com dados do cliente")
public class CriadoClienteResponseDTO {

    @Schema(description = "ID único do cliente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "João Silva")
    private String nome;

    @Schema(description = "CPF do cliente", example = "12345678901")
    private String cpf;

    @Schema(description = "Email do cliente", example = "joao@email.com")
    private String email;

    @Schema(description = "Telefone do cliente", example = "11999999999")
    private String telefone;

    @Schema(description = "Status do cliente", example = "ATIVO")
    private StatusClienteEnum status;

    @Schema(description = "Data e hora de cadastro", example = "2024-09-27T10:30:00")
    private LocalDateTime dataCadastro;

    public static CriadoClienteResponseDTO fromEntity(Cliente cliente) {
        return CriadoClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .status(cliente.getStatus())
                .dataCadastro(cliente.getDataCadastro())
                .build();
    }
}