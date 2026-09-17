package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Banner;

public record BannerResponse(
        Long id,
        String titulo,
        String imagem,
        Boolean ativo) {
    public static BannerResponse de(Banner banner) {
        if (banner == null) {
            return null;
        }

        return new BannerResponse(
                banner.getId(),
                banner.getTitulo(),
                banner.getImagem(),
                banner.getAtivo());
    }
}
