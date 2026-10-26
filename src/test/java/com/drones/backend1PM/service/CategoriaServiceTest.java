package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.CategoriaRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Drones Profissionais");
    }

    @Test
    void deveSalvarCategoriaComSucesso() {
        CategoriaRequest request = new CategoriaRequest("Drones Profissionais");

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = categoriaService.salvar(request);

        assertNotNull(resultado);
        assertEquals("Drones Profissionais", resultado.getNome());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void deveListarCategorias() {
        Categoria outraCategoria = new Categoria();
        outraCategoria.setId(2L);
        outraCategoria.setNome("Acessórios");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria, outraCategoria));

        List<Categoria> resultado = categoriaService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Drones Profissionais", resultado.get(0).getNome());
        assertEquals("Acessórios", resultado.get(1).getNome());
        verify(categoriaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<Categoria> resultado = categoriaService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(categoriaRepository).findAll();
    }

    @Test
    void deveBuscarCategoriaPorIdComSucesso() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Optional<Categoria> resultado = categoriaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Drones Profissionais", resultado.get().getNome());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void deveRetornarOptionalVazioQuandoCategoriaNaoExistir() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Categoria> resultado = categoriaService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(categoriaRepository).findById(99L);
    }

    @Test
    void deveAtualizarCategoriaComSucesso() {
        CategoriaRequest request = new CategoriaRequest("Drones Atualizados");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Categoria resultado = categoriaService.atualizar(1L, request);

        assertNotNull(resultado);
        assertEquals("Drones Atualizados", resultado.getNome());
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        CategoriaRequest request = new CategoriaRequest("Categoria Inexistente");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoriaService.atualizar(99L, request)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveExcluirCategoriaComSucesso() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> categoriaService.excluir(1L));

        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirCategoriaInexistente() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoriaService.excluir(99L)
        );

        assertEquals("Categoria não encontrado", exception.getMessage());
        verify(categoriaRepository, never()).deleteById(anyLong());
    }
}