package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Conta;

public record ContaResponse(
        Long id,
        String nome,
        String email,
        String telefone) {
    public static ContaResponse de(Conta conta) {
        if (conta == null) {
            return null;
        }

        return new ContaResponse(
                conta.getId(),
                conta.getNome(),
                conta.getEmail(),
                conta.getTelefone());
    }
}
