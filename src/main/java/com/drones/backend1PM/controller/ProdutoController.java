package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.ProdutoRequest;
import com.drones.backend1PM.dto.ProdutoResponse;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/cadastrar")
    public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody ProdutoRequest produtoRequest) {
        try {
            Produto produtoEntity = this.produtoService.salvar(produtoRequest);
            return new ResponseEntity<>(ProdutoResponse.de(produtoEntity), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProdutoResponse>> listar() {
        try {
            List<ProdutoResponse> produtoList = this.produtoService.listar()
                    .stream()
                    .map(ProdutoResponse::de)
                    .toList();

            return new ResponseEntity<>(produtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/buscarpornome")
    public ResponseEntity<List<ProdutoResponse>> buscarPorNome(@RequestParam String nome) {
        try {
            List<ProdutoResponse> produtoList = this.produtoService.buscarPorNome(nome)
                    .stream()
                    .map(ProdutoResponse::de)
                    .toList();

            return new ResponseEntity<>(produtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Produto> produtoOptional = this.produtoService.buscarPorId(id);
            if (produtoOptional.isPresent()) {
                return new ResponseEntity<>(ProdutoResponse.de(produtoOptional.get()), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequest produtoRequest) {
        try {
            Produto produtoEntity = this.produtoService.atualizar(id, produtoRequest);
            return new ResponseEntity<>(ProdutoResponse.de(produtoEntity), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            this.produtoService.excluir(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}


