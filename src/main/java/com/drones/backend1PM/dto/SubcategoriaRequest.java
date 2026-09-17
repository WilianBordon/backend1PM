package com.drones.backend1PM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubcategoriaRequest(
        @NotBlank(message = "Nome da subcategoria é obrigatório") String nome,
        @NotNull(message = "Categoria é obrigatória") Long categoriaId) {
}
