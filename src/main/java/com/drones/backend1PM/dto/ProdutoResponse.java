package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Produto;

public record ProdutoResponse (
        Long id,
        String nome,
        String descricao,
        Double preco,
        Integer quantidade
) {
    public static ProdutoResponse de(Produto produto) {
        if (produto == null) {
            return null;
        }
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade()
        );
    }
}
