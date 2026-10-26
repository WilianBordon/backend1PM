package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.SubcategoriaRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
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
class SubcategoriaServiceTest {

    @Mock
    private SubcategoriaRepository subcategoriaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private SubcategoriaService subcategoriaService;

    private Categoria categoria;
    private Subcategoria subcategoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Drones");

        subcategoria = new Subcategoria();
        subcategoria.setId(1L);
        subcategoria.setNome("Baterias");
        subcategoria.setCategoria(categoria);
    }

    @Test
    void deveSalvarSubcategoriaComSucesso() {
        SubcategoriaRequest request = new SubcategoriaRequest("Baterias", 1L);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.save(any(Subcategoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Subcategoria resultado = subcategoriaService.salvar(request);

        assertNotNull(resultado);
        assertEquals("Baterias", resultado.getNome());
        assertEquals(categoria, resultado.getCategoria());
        verify(categoriaRepository).findById(1L);
        verify(subcategoriaRepository).save(any(Subcategoria.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarSubcategoriaComCategoriaInexistente() {
        SubcategoriaRequest request = new SubcategoriaRequest("Baterias", 99L);

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subcategoriaService.salvar(request)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(subcategoriaRepository, never()).save(any(Subcategoria.class));
    }

    @Test
    void deveListarSubcategorias() {
        Subcategoria outraSubcategoria = new Subcategoria();
        outraSubcategoria.setId(2L);
        outraSubcategoria.setNome("Hélices");
        outraSubcategoria.setCategoria(categoria);

        when(subcategoriaRepository.findAll()).thenReturn(List.of(subcategoria, outraSubcategoria));

        List<Subcategoria> resultado = subcategoriaService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Baterias", resultado.get(0).getNome());
        assertEquals("Hélices", resultado.get(1).getNome());
        verify(subcategoriaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremSubcategorias() {
        when(subcategoriaRepository.findAll()).thenReturn(List.of());

        List<Subcategoria> resultado = subcategoriaService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(subcategoriaRepository).findAll();
    }

    @Test
    void deveBuscarSubcategoriaPorIdComSucesso() {
        when(subcategoriaRepository.findById(1L)).thenReturn(Optional.of(subcategoria));

        Optional<Subcategoria> resultado = subcategoriaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Baterias", resultado.get().getNome());
        verify(subcategoriaRepository).findById(1L);
    }

    @Test
    void deveRetornarOptionalVazioQuandoSubcategoriaNaoExistir() {
        when(subcategoriaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Subcategoria> resultado = subcategoriaService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(subcategoriaRepository).findById(99L);
    }

    @Test
    void deveAtualizarSubcategoriaComSucesso() {
        SubcategoriaRequest request = new SubcategoriaRequest("Hélices Avançadas", 1L);

        when(subcategoriaRepository.findById(1L)).thenReturn(Optional.of(subcategoria));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.save(any(Subcategoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Subcategoria resultado = subcategoriaService.atualizar(1L, request);

        assertNotNull(resultado);
        assertEquals("Hélices Avançadas", resultado.getNome());
        verify(subcategoriaRepository).findById(1L);
        verify(categoriaRepository).findById(1L);
        verify(subcategoriaRepository).save(subcategoria);
    }

    @Test
    void deveLancarExcecaoAoAtualizarSubcategoriaInexistente() {
        SubcategoriaRequest request = new SubcategoriaRequest("Nome", 1L);

        when(subcategoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subcategoriaService.atualizar(99L, request)
        );

        assertEquals("Subcategoria não encontrada", exception.getMessage());
        verify(subcategoriaRepository, never()).save(any(Subcategoria.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarSubcategoriaComCategoriaInexistente() {
        SubcategoriaRequest request = new SubcategoriaRequest("Nome", 99L);

        when(subcategoriaRepository.findById(1L)).thenReturn(Optional.of(subcategoria));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subcategoriaService.atualizar(1L, request)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(subcategoriaRepository, never()).save(any(Subcategoria.class));
    }

    @Test
    void deveExcluirSubcategoriaComSucesso() {
        when(subcategoriaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> subcategoriaService.excluir(1L));

        verify(subcategoriaRepository).existsById(1L);
        verify(subcategoriaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirSubcategoriaInexistente() {
        when(subcategoriaRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subcategoriaService.excluir(99L)
        );

        assertEquals("Subcategoria não encontrado", exception.getMessage());
        verify(subcategoriaRepository, never()).deleteById(anyLong());
    }
}