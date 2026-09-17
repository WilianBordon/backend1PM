package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.ContaRequest;
import com.drones.backend1PM.dto.ContaResponse;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaResponse> cadastrar(@Valid @RequestBody ContaRequest request) {
        Conta conta = contaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContaResponse.de(conta));
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar().stream().map(ContaResponse::de).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return contaService.buscarPorId(id)
                .map(conta -> ResponseEntity.ok(ContaResponse.de(conta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ContaRequest request) {
        Conta conta = contaService.atualizar(id, request);
        return ResponseEntity.ok(ContaResponse.de(conta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        contaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
