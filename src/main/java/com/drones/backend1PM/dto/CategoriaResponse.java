package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Categoria;

public record CategoriaResponse(
        Long id,
        String nome) {
    public static CategoriaResponse de(Categoria categoria) {
        if (categoria == null) {
            return null;
        }

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome());
    }
}
