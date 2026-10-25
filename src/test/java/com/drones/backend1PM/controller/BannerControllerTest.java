
package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.BannerRequest;
import com.drones.backend1PM.entity.Banner;
import com.drones.backend1PM.service.BannerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BannerController.class)
class BannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BannerService bannerService;

    private Banner criarBanner() {
        Banner banner = new Banner();
        banner.setId(1L);
        banner.setTitulo("Banner principal");
        banner.setImagem("banner.jpg");
        banner.setAtivo(true);
        return banner;
    }

    private BannerRequest criarRequest() {
        return new BannerRequest(
                "Banner principal",
                "banner.jpg",
                true
        );
    }

    @Test
    void deveCadastrarBanner() throws Exception {
        when(bannerService.salvar(any(BannerRequest.class)))
                .thenReturn(criarBanner());

        mockMvc.perform(post("/banners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Banner principal"))
                .andExpect(jsonPath("$.imagem").value("banner.jpg"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(bannerService).salvar(any(BannerRequest.class));
    }

    @Test
    void deveListarBanners() throws Exception {
        when(bannerService.listar())
                .thenReturn(List.of(criarBanner()));

        mockMvc.perform(get("/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Banner principal"));

        verify(bannerService).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremBanners()
            throws Exception {
        when(bannerService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bannerService).listar();
    }

    @Test
    void deveBuscarBannerPorId() throws Exception {
        when(bannerService.buscarPorId(1L))
                .thenReturn(Optional.of(criarBanner()));

        mockMvc.perform(get("/banners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Banner principal"));

        verify(bannerService).buscarPorId(1L);
    }

    @Test
    void deveRetornar404QuandoBannerNaoForEncontrado()
            throws Exception {
        when(bannerService.buscarPorId(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/banners/99"))
                .andExpect(status().isNotFound());

        verify(bannerService).buscarPorId(99L);
    }

    @Test
    void deveAtualizarBanner() throws Exception {
        Banner atualizado = criarBanner();
        atualizado.setTitulo("Banner atualizado");
        atualizado.setImagem("banner-novo.jpg");

        when(bannerService.atualizar(
                eq(1L), any(BannerRequest.class)))
                .thenReturn(atualizado);

        BannerRequest request = new BannerRequest(
                "Banner atualizado",
                "banner-novo.jpg",
                true
        );

        mockMvc.perform(put("/banners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Banner atualizado"))
                .andExpect(jsonPath("$.imagem").value("banner-novo.jpg"));

        verify(bannerService)
                .atualizar(eq(1L), any(BannerRequest.class));
    }

    @Test
    void deveExcluirBanner() throws Exception {
        doNothing().when(bannerService).excluir(1L);

        mockMvc.perform(delete("/banners/1"))
                .andExpect(status().isNoContent());

        verify(bannerService).excluir(1L);
    }
}
