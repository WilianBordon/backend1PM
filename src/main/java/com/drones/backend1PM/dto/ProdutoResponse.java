package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Produto;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        Double preco,
        Integer quantidade,
        Double altura,
        Double largura,
        Double diametro,
        Double circunferencia,
        Double pesoAprox,
        String imagemPrincipal,
        Boolean ativo,
        Boolean destaque,
        Long categoriaId,
        Long subcategoriaId) {
    public static ProdutoResponse de(Produto produto) {
        if (produto == null) {
            return null;
        }

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade(),
                produto.getAltura(),
                produto.getLargura(),
                produto.getDiametro(),
                produto.getCircunferencia(),
                produto.getPesoAprox(),
                produto.getImagemPrincipal(),
                produto.getAtivo(),
                produto.getDestaque(),
                produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                produto.getSubcategoria() != null ? produto.getSubcategoria().getId() : null);
    }
}
