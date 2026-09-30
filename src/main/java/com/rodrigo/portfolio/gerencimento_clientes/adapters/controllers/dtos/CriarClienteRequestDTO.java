package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para criar um novo cliente")
public class CriarClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Schema(description = "Nome completo do cliente", example = "João Silva", minLength = 3, maxLength = 150)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter apenas números")
    @Schema(description = "CPF do cliente (11 dígitos)", example = "12345678901")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email do cliente", example = "joao@email.com", format = "email")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 10, max = 20, message = "Telefone inválido")
    @Schema(description = "Telefone do cliente", example = "11999999999", minLength = 10, maxLength = 20)
    private String telefone;
}