package com.rodrigo.portfolio.gerencimento_clientes.domain.enums;

import lombok.Getter;

@Getter
public enum StatusClienteEnum {

    ATIVO("Ativo"),
    INATIVO("Inativo"),
    SUSPENSO("Suspenso");

    private final String descricao;

    StatusClienteEnum(String descricao) {
        this.descricao = descricao;
    }

}
