package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.CategoriaRequest;
import com.drones.backend1PM.dto.CategoriaResponse;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponse> cadastrar(@Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = categoriaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse.de(categoria));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar().stream().map(CategoriaResponse::de).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(categoria -> ResponseEntity.ok(CategoriaResponse.de(categoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = categoriaService.atualizar(id, request);
        return ResponseEntity.ok(CategoriaResponse.de(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
