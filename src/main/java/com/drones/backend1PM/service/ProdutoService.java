package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // 6 END-POINTS = SALVAR, LISTAR, BUSCAR ID, ATUALIZAR, EXCLUIR e BUSCAR P NOME

    public Produto salvar(ProdutoRequest produtoRequest) {

        Produto produto = new Produto();

        produto.setNome(produtoRequest.nome());
        produto.setDescricao(produtoRequest.descricao());
        produto.setPreco(produtoRequest.preco());
        produto.setQuantidade(produtoRequest.quantidade());

        return this.produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return this.produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return this.produtoRepository.findById(id);
    }

    public Produto atualizar(Long id, ProdutoRequest produtoRequest) {

        Produto produto = new Produto();

        produto.setId(id);
        produto.setNome(produtoRequest.nome());
        produto.setDescricao(produtoRequest.descricao());
        produto.setPreco(produtoRequest.preco());
        produto.setQuantidade(produtoRequest.quantidade());

        return this.produtoRepository.save(produto);
    }

    public void excluir(Long id) {
        this.produtoRepository.deleteById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return this.produtoRepository.findByNomeContainingIgnoreCase(nome);
    }
}