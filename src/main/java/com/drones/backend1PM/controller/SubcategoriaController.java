package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.SubcategoriaRequest;
import com.drones.backend1PM.dto.SubcategoriaResponse;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.service.SubcategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/subcategorias")
@RequiredArgsConstructor
public class SubcategoriaController {

    private final SubcategoriaService subcategoriaService;

    @PostMapping
    public ResponseEntity<SubcategoriaResponse> cadastrar(@Valid @RequestBody SubcategoriaRequest request) {
        Subcategoria subcategoria = subcategoriaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SubcategoriaResponse.de(subcategoria));
    }

    @GetMapping
    public ResponseEntity<List<SubcategoriaResponse>> listar() {
        return ResponseEntity.ok(subcategoriaService.listar().stream().map(SubcategoriaResponse::de).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> buscarPorId(@PathVariable Long id) {
        return subcategoriaService.buscarPorId(id)
                .map(subcategoria -> ResponseEntity.ok(SubcategoriaResponse.de(subcategoria)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubcategoriaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody SubcategoriaRequest request) {
        Subcategoria subcategoria = subcategoriaService.atualizar(id, request);
        return ResponseEntity.ok(SubcategoriaResponse.de(subcategoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        subcategoriaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
