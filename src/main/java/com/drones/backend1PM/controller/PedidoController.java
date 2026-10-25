package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.PedidoRequest;
import com.drones.backend1PM.dto.PedidoResponse;
import com.drones.backend1PM.entity.Pedido;
import com.drones.backend1PM.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> cadastrar(@Valid @RequestBody PedidoRequest request) {
        Pedido pedido = pedidoService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.de(pedido));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        return ResponseEntity.ok(pedidoService.listar().stream().map(PedidoResponse::de).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(PedidoResponse.de(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
