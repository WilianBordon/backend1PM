package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.BannerRequest;
import com.drones.backend1PM.entity.Banner;
import com.drones.backend1PM.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    public Banner salvar(BannerRequest request) {
        Banner banner = new Banner();
        banner.setTitulo(request.titulo());
        banner.setImagem(request.imagem());
        banner.setAtivo(request.ativo() != null ? request.ativo() : true);
        log.info("Banner criado com sucesso.");
        return bannerRepository.save(banner);
    }

    public List<Banner> listar() {

        log.info("Buscando todos os banners.");

        List<Banner> banners = bannerRepository.findAll();

        log.info("Foram encontrados {} banners", banners.size());

        return banners;

    }

    public Optional<Banner> buscarPorId(Long id) {

        log.info("Buscando Banner pelo ID: {}", id);

        Optional<Banner> banner = bannerRepository.findById(id);

        if (banner.isPresent()) {
            log.info("Banner encontrado. ID: {}", id);
        } else {
            log.warn("Banner não encontrado. ID: {}", id);
        }

        return banner;

    }

    public Banner atualizar(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Banner não encontrado.");
                    return new RuntimeException("Banner não encontrado");
                });
        banner.setTitulo(request.titulo());
        banner.setImagem(request.imagem());
        banner.setAtivo(request.ativo() != null ? request.ativo() : banner.getAtivo());
        return bannerRepository.save(banner);
    }

    public void excluir(Long id) {
        log.info("Iniciando exclusão do produto. ID: {}", id);

        if (!bannerRepository.existsById(id)) {
            log.warn("Tentativa de excluir banner inexistente. ID: {}", id);
            throw new RuntimeException("Banner não encontrado");
        }

        bannerRepository.deleteById(id);

        log.info("Banner excluído com sucesso. ID: {}", id);

    }
}
