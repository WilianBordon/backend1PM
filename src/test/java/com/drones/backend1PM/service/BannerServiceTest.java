
package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.BannerRequest;
import com.drones.backend1PM.entity.Banner;
import com.drones.backend1PM.repository.BannerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @InjectMocks
    private BannerService bannerService;

    private Banner banner;

    @BeforeEach
    void setUp() {
        banner = new Banner();
        banner.setId(1L);
        banner.setTitulo("Banner principal");
        banner.setImagem("banner.jpg");
        banner.setAtivo(true);
    }


    @Test
    void deveSalvarBannerComSucesso() {
        BannerRequest request = new BannerRequest(
                "Banner principal",
                "banner.jpg",
                true
        );

        when(bannerRepository.save(any(Banner.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Banner resultado = bannerService.salvar(request);

        assertNotNull(resultado);
        assertEquals("Banner principal", resultado.getTitulo());
        assertEquals("banner.jpg", resultado.getImagem());
        assertTrue(resultado.getAtivo());

        verify(bannerRepository).save(any(Banner.class));
    }


    @Test
    void deveSalvarBannerAtivoQuandoAtivoForNulo() {
        BannerRequest request = new BannerRequest(
                "Banner promocional",
                "promocao.jpg",
                null
        );

        when(bannerRepository.save(any(Banner.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Banner resultado = bannerService.salvar(request);

        assertTrue(resultado.getAtivo());
        assertEquals("Banner promocional", resultado.getTitulo());

        verify(bannerRepository).save(any(Banner.class));
    }


    @Test
    void deveListarBanners() {
        Banner outroBanner = new Banner();
        outroBanner.setId(2L);
        outroBanner.setTitulo("Banner secundário");
        outroBanner.setImagem("secundario.jpg");
        outroBanner.setAtivo(false);

        when(bannerRepository.findAll())
                .thenReturn(List.of(banner, outroBanner));

        List<Banner> resultado = bannerService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Banner principal", resultado.get(0).getTitulo());
        assertEquals("Banner secundário", resultado.get(1).getTitulo());

        verify(bannerRepository).findAll();
    }


    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremBanners() {
        when(bannerRepository.findAll()).thenReturn(List.of());

        List<Banner> resultado = bannerService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(bannerRepository).findAll();
    }


    @Test
    void deveBuscarBannerPorId() {
        when(bannerRepository.findById(1L))
                .thenReturn(Optional.of(banner));

        Optional<Banner> resultado = bannerService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Banner principal", resultado.get().getTitulo());
        assertEquals(1L, resultado.get().getId());

        verify(bannerRepository).findById(1L);
    }


    @Test
    void deveRetornarOptionalVazioQuandoBannerNaoExistir() {
        when(bannerRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Banner> resultado = bannerService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(bannerRepository).findById(99L);
    }


    @Test
    void deveAtualizarBannerComSucesso() {
        BannerRequest request = new BannerRequest(
                "Banner atualizado",
                "atualizado.jpg",
                false
        );

        when(bannerRepository.findById(1L))
                .thenReturn(Optional.of(banner));

        when(bannerRepository.save(any(Banner.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Banner resultado = bannerService.atualizar(1L, request);

        assertEquals("Banner atualizado", resultado.getTitulo());
        assertEquals("atualizado.jpg", resultado.getImagem());
        assertFalse(resultado.getAtivo());

        verify(bannerRepository).findById(1L);
        verify(bannerRepository).save(banner);
    }


    @Test
    void deveManterStatusAnteriorQuandoAtivoForNuloNaAtualizacao() {
        banner.setAtivo(false);

        BannerRequest request = new BannerRequest(
                "Banner atualizado",
                "atualizado.jpg",
                null
        );

        when(bannerRepository.findById(1L))
                .thenReturn(Optional.of(banner));

        when(bannerRepository.save(any(Banner.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Banner resultado = bannerService.atualizar(1L, request);

        assertFalse(resultado.getAtivo());
        assertEquals("Banner atualizado", resultado.getTitulo());

        verify(bannerRepository).save(banner);
    }


    @Test
    void deveLancarExcecaoAoAtualizarBannerInexistente() {
        BannerRequest request = new BannerRequest(
                "Banner atualizado",
                "atualizado.jpg",
                true
        );

        when(bannerRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bannerService.atualizar(99L, request)
        );

        assertEquals("Banner não encontrado", exception.getMessage());

        verify(bannerRepository, never()).save(any(Banner.class));
    }


    @Test
    void deveExcluirBannerComSucesso() {
        when(bannerRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bannerService.excluir(1L));

        verify(bannerRepository).existsById(1L);
        verify(bannerRepository).deleteById(1L);
    }


    @Test
    void deveLancarExcecaoAoExcluirBannerInexistente() {
        when(bannerRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bannerService.excluir(99L)
        );

        assertEquals("Banner não encontrado", exception.getMessage());

        verify(bannerRepository, never()).deleteById(anyLong());
    }
}
