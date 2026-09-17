package com.drones.backend1PM.repository;

import com.drones.backend1PM.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByEmail(String email);
}
