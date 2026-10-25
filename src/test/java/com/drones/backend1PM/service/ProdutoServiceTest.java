package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.ProdutoRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private SubcategoriaRepository subcategoriaRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Categoria categoria;
    private Subcategoria subcategoria;
    private Produto produto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Cozinha");

        subcategoria = new Subcategoria();
        subcategoria.setId(1L);
        subcategoria.setNome("Canecas");
        subcategoria.setCategoria(categoria);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Caneca");
        produto.setDescricao("Caneca personalizada");
        produto.setPreco(35.90);
        produto.setQuantidade(10);
        produto.setImagemPrincipal("caneca.jpg");
        produto.setAtivo(true);
        produto.setDestaque(false);
        produto.setCategoria(categoria);
        produto.setSubcategoria(subcategoria);
    }

    private ProdutoRequest criarRequest(
            Boolean ativo,
            Boolean destaque,
            Long subcategoriaId) {

        return new ProdutoRequest(
                "Caneca personalizada",
                "Caneca com nome",
                45.90,
                20,
                10.0,
                8.0,
                null,
                null,
                0.5,
                "caneca.jpg",
                ativo,
                destaque,
                1L,
                subcategoriaId
        );
    }

    @Test
    void deveSalvarProdutoComValoresPadrao() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.salvar(
                criarRequest(null, null, null));

        assertNotNull(resultado);
        assertEquals("Caneca personalizada", resultado.getNome());
        assertTrue(resultado.getAtivo());
        assertFalse(resultado.getDestaque());
        assertEquals(categoria, resultado.getCategoria());
        assertNull(resultado.getSubcategoria());

        verify(subcategoriaRepository, never()).findById(anyLong());
        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    void deveSalvarProdutoComAtivoEDestaqueInformados() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.findById(1L))
                .thenReturn(Optional.of(subcategoria));
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.salvar(
                criarRequest(false, true, 1L));

        assertFalse(resultado.getAtivo());
        assertTrue(resultado.getDestaque());
        assertEquals(subcategoria, resultado.getSubcategoria());
    }

    @Test
    void deveSalvarProdutoComAtivoEDestaqueExplicitamenteFalsos() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.salvar(
                criarRequest(false, false, null));

        assertFalse(resultado.getAtivo());
        assertFalse(resultado.getDestaque());
    }

    @Test
    void deveLancarExcecaoAoSalvarSemCategoriaExistente() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> produtoService.salvar(
                        criarRequest(true, false, null)));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoSalvarComSubcategoriaInexistente() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> produtoService.salvar(
                        criarRequest(true, false, 99L)));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveAtualizarProdutoPreservandoAtivoEDestaqueQuandoNulos() {
        produto.setAtivo(true);
        produto.setDestaque(true);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.atualizar(
                1L, criarRequest(null, null, null));

        assertEquals("Caneca personalizada", resultado.getNome());
        assertEquals(45.90, resultado.getPreco());
        assertEquals(20, resultado.getQuantidade());
        assertTrue(resultado.getAtivo());
        assertTrue(resultado.getDestaque());
        assertNull(resultado.getSubcategoria());
    }

    @Test
    void deveAtualizarAtivoEDestaqueQuandoInformados() {
        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.findById(1L))
                .thenReturn(Optional.of(subcategoria));
        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.atualizar(
                1L, criarRequest(false, true, 1L));

        assertFalse(resultado.getAtivo());
        assertTrue(resultado.getDestaque());
        assertEquals(subcategoria, resultado.getSubcategoria());
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> produtoService.atualizar(
                        99L, criarRequest(true, false, null)));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCategoriaInexistente() {
        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> produtoService.atualizar(
                        1L, criarRequest(true, false, null)));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComSubcategoriaInexistente() {
        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(subcategoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> produtoService.atualizar(
                        1L, criarRequest(true, false, 99L)));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    void deveExcluirProdutoExistente() {
        when(produtoRepository.existsById(1L))
                .thenReturn(true);

        assertDoesNotThrow(() -> produtoService.excluir(1L));

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        when(produtoRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> produtoService.excluir(99L));

        verify(produtoRepository, never()).deleteById(99L);
    }
}