package com.drones.backend1PM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoRequest(
                @NotBlank(message = "Nome do produto é obrigatório") String nome,
                String descricao,
                @NotNull(message = "Preço é obrigatório") @Positive(message = "Preço deve ser maior que zero") Double preco,
                @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser maior que zero") Integer quantidade,
                Double altura,
                Double largura,
                Double diametro,
                Double circunferencia,
                Double pesoAprox,
                @NotBlank(message = "Imagem principal é obrigatória") String imagemPrincipal,
                Boolean ativo,
                Boolean destaque,
                @NotNull(message = "Categoria é obrigatória") Long categoriaId,
                Long subcategoriaId) {
}
