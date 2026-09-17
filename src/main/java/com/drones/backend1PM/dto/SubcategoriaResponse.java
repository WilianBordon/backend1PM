package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Subcategoria;

public record SubcategoriaResponse(
        Long id,
        String nome,
        Long categoriaId) {
    public static SubcategoriaResponse de(Subcategoria subcategoria) {
        if (subcategoria == null) {
            return null;
        }

        return new SubcategoriaResponse(
                subcategoria.getId(),
                subcategoria.getNome(),
                subcategoria.getCategoria() != null ? subcategoria.getCategoria().getId() : null);
    }
}
