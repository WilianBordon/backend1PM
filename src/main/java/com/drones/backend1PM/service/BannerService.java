package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.BannerRequest;
import com.drones.backend1PM.entity.Banner;
import com.drones.backend1PM.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public Banner salvar(BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitulo(request.titulo());
        banner.setImagem(request.imagem());
        banner.setAtivo(request.ativo() != null ? request.ativo() : true);
        return bannerRepository.save(banner);
    }

    public List<Banner> listar() {
        return bannerRepository.findAll();
    }

    public Optional<Banner> buscarPorId(Long id) {
        return bannerRepository.findById(id);
    }

    public Banner atualizar(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner não encontrado"));
        banner.setTitulo(request.titulo());
        banner.setImagem(request.imagem());
        banner.setAtivo(request.ativo() != null ? request.ativo() : banner.getAtivo());
        return bannerRepository.save(banner);
    }

    public void excluir(Long id) {
        bannerRepository.deleteById(id);
    }
}
