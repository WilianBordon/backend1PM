package com.drones.backend1PM.dto;

public record ProdutoRequest (
        String nome,
        String descricao,
        Double preco,
        Integer quantidade
) {
}
