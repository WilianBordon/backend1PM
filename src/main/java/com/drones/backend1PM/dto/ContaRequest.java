package com.drones.backend1PM.dto;

import jakarta.validation.constraints.NotBlank;

public record ContaRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        String email,
        @NotBlank(message = "Senha é obrigatória") String senha,
        @NotBlank(message = "Telefone é obrigatório") String telefone) {
}
