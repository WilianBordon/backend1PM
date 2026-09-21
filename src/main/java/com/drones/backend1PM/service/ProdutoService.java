package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.ProdutoRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdutoService {
// as
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;

    public Produto salvar(ProdutoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada. ID: {}", request.categoriaId());
                    return new RuntimeException("Categoria não encontrada");
                });

        Subcategoria subcategoria = null;
        if (request.subcategoriaId() != null) {

            log.info("Buscando subcategoria. ID: {}", request.subcategoriaId());

            subcategoria = subcategoriaRepository.findById(request.subcategoriaId())

                    .orElseThrow(() -> {
                        log.warn("Subcategoria não encontrada. ID: {}",request.subcategoriaId());
                            return new RuntimeException("Subcategoria não encontrada");
                    });
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

        Produto produtoSalvo = produtoRepository.save(produto);
        log.info("Produto cadastrado com sucesso. ID: {}, Nome: {}", produtoSalvo.getId(), produtoSalvo.getNome());

        return produtoSalvo;
    }

    public List<Produto> listar() {

        log.info("Buscando todos os produtos");

        List<Produto> produtos = produtoRepository.findAll();

        log.info("Foram encontrados {} produtos", produtos.size());

        return produtos;
    }

    public Optional<Produto> buscarPorId(Long id) {
        log.info("Buscando produto pelo ID: {}", id);

        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isPresent()) {
            log.info("Produto encontrado. ID: {}", id);
        } else {
            log.warn("Produto não encontrado. ID: {}", id);
        }

        return produto;
    }

    public List<Produto> buscarPorNome(String nome) {
        log.info("Buscando produtos pelo nome: {}", nome);

        List<Produto> produtos =
                produtoRepository.findByNomeContainingIgnoreCase(nome);

        log.info("Foram encontrados {} produtos para a busca '{}'",
                produtos.size(),
                nome);

        return produtos;
    }

    public List<Produto> listarPorCategoria(Long categoriaId) {

        log.info("Buscando produtos da categoria. ID: {}", categoriaId);

        List<Produto> produtos =
                produtoRepository.findByCategoriaId(categoriaId);

        log.info("Foram encontrados {} produtos na categoria {}",
                produtos.size(),
                categoriaId);

        return produtos;
    }

    public List<Produto> listarPorSubcategoria(Long subcategoriaId) {

        log.info("Buscando produtos da subcategoria. ID: {}", subcategoriaId);

        List<Produto> produtos =
                produtoRepository.findBySubcategoriaId(subcategoriaId);

        log.info("Foram encontrados {} produtos na subcategoria {}",
                produtos.size(),
                subcategoriaId);

        return produtos;
    }

    public Produto atualizar(Long id, ProdutoRequest request) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado para atualização. ID: {}", id);
                    return new RuntimeException("Produto não encontrado");

                });

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada durante atualização. ID: {}", request.categoriaId());
                    return new RuntimeException("Categoria não encontrada");
                });

        Subcategoria subcategoria = null;
        if (request.subcategoriaId() != null) {
            subcategoria = subcategoriaRepository.findById(request.subcategoriaId())
                    .orElseThrow(() -> {
                        log.warn("Subcategoria não encontrada durante atualização. ID: {}", request.subcategoriaId());
                        return new RuntimeException("Subcategoria não encontrada");
                    });
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

        log.info("Produto atualizado com sucesso. ID: {}", id);

        return produtoRepository.save(produto);
    }

    public void excluir(Long id) {

        if (!produtoRepository.existsById(id)) {
            log.warn("Tentativa de excluir produto inexistente. ID: {}", id);
            throw new RuntimeException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);

        log.info("Produto excluído com sucesso. ID: {}", id);
    }
}