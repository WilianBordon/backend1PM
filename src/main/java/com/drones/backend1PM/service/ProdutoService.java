package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.ProdutoRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;

    public Produto salvar(ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Subcategoria subcategoria = null;
        if (request.subcategoriaId() != null) {
            subcategoria = subcategoriaRepository.findById(request.subcategoriaId())
                    .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));
        }

        Produto produto = new Produto();
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setQuantidade(request.quantidade());
        produto.setAltura(request.altura());
        produto.setLargura(request.largura());
        produto.setDiametro(request.diametro());
        produto.setCircunferencia(request.circunferencia());
        produto.setPesoAprox(request.pesoAprox());
        produto.setImagemPrincipal(request.imagemPrincipal());
        produto.setAtivo(request.ativo() != null ? request.ativo() : true);
        produto.setDestaque(request.destaque() != null ? request.destaque() : false);
        produto.setCategoria(categoria);
        produto.setSubcategoria(subcategoria);

        return produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Subcategoria subcategoria = null;
        if (request.subcategoriaId() != null) {
            subcategoria = subcategoriaRepository.findById(request.subcategoriaId())
                    .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));
        }

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setQuantidade(request.quantidade());
        produto.setAltura(request.altura());
        produto.setLargura(request.largura());
        produto.setDiametro(request.diametro());
        produto.setCircunferencia(request.circunferencia());
        produto.setPesoAprox(request.pesoAprox());
        produto.setImagemPrincipal(request.imagemPrincipal());
        produto.setAtivo(request.ativo() != null ? request.ativo() : produto.getAtivo());
        produto.setDestaque(request.destaque() != null ? request.destaque() : produto.getDestaque());
        produto.setCategoria(categoria);
        produto.setSubcategoria(subcategoria);

        return produtoRepository.save(produto);
    }

    public void excluir(Long id) {
        produtoRepository.deleteById(id);
    }
}