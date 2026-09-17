package com.drones.backend1PM.repository;

import com.drones.backend1PM.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
