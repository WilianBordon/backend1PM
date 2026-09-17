package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.dto.ProdutoResponse;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(@Valid @RequestBody ProdutoRequest request) {
        Produto produto = this.produtoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.de(produto));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        List<ProdutoResponse> produtoList = this.produtoService.listar()
                .stream()
                .map(ProdutoResponse::de)
                .toList();

        return ResponseEntity.ok(produtoList);
    }

    @GetMapping("/buscarpornome")
    public ResponseEntity<List<ProdutoResponse>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoResponse> produtoList = this.produtoService.buscarPorNome(nome)
                .stream()
                .map(ProdutoResponse::de)
                .toList();

        return ResponseEntity.ok(produtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return this.produtoService.buscarPorId(id)
                .map(produto -> ResponseEntity.ok(ProdutoResponse.de(produto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request) {
        Produto produto = this.produtoService.atualizar(id, request);
        return ResponseEntity.ok(ProdutoResponse.de(produto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        this.produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
