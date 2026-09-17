package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.BannerRequest;
import com.drones.backend1PM.dto.BannerResponse;
import com.drones.backend1PM.entity.Banner;
import com.drones.backend1PM.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    public ResponseEntity<BannerResponse> cadastrar(@Valid @RequestBody BannerRequest request) {
        Banner banner = bannerService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BannerResponse.de(banner));
    }

    @GetMapping
    public ResponseEntity<List<BannerResponse>> listar() {
        return ResponseEntity.ok(bannerService.listar().stream().map(BannerResponse::de).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BannerResponse> buscarPorId(@PathVariable Long id) {
        return bannerService.buscarPorId(id)
                .map(banner -> ResponseEntity.ok(BannerResponse.de(banner)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BannerResponse> atualizar(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        Banner banner = bannerService.atualizar(id, request);
        return ResponseEntity.ok(BannerResponse.de(banner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bannerService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
