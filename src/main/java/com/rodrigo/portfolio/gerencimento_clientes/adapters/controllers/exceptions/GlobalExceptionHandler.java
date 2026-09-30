package com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.exceptions;

import com.rodrigo.portfolio.gerencimento_clientes.adapters.controllers.exceptions.dtos.ErroResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponseDTO> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        log.error("Violação de integridade de dados: {}", ex.getMessage());

        String mensagem = extrairMensagemErro(ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErroResponseDTO("Dados duplicados", mensagem));
    }

    private String extrairMensagemErro(DataIntegrityViolationException ex) {
        String msg = ex.getMessage();

        if (msg != null) {
            if (msg.contains("clientes.cpf") || msg.contains("clientes_cpf")) {
                return "CPF já cadastrado no sistema";
            }
            if (msg.contains("clientes.email") || msg.contains("clientes_email")) {
                return "Email já cadastrado no sistema";
            }
            if (msg.contains("UNIQUE constraint failed")) {
                return "Este valor já existe no sistema";
            }
        }

        return "Erro de integridade de dados. Verifique os dados enviados.";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("Erro de validação: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO("Erro de validação", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponseDTO> handleRuntimeException(RuntimeException ex) {
        log.error("Erro interno: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErroResponseDTO("Não encontrado", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Erro de validação de entrada: {}", mensagem);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponseDTO("Erro de validação", mensagem));
    }
}
