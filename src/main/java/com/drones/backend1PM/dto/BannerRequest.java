package com.drones.backend1PM.dto;

import jakarta.validation.constraints.NotBlank;

public record BannerRequest(
        @NotBlank(message = "Título do banner é obrigatório") String titulo,
        @NotBlank(message = "Imagem do banner é obrigatória") String imagem,
        Boolean ativo) {
}
