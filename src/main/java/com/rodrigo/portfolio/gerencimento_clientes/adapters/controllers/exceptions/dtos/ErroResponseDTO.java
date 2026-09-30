package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.exceptions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErroResponseDTO {
    private String titulo;
    private String mensagem;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErroResponseDTO(String dadosDuplicados, String mensagem) {
        this.titulo = dadosDuplicados;
        this.mensagem = mensagem;
    }
}
