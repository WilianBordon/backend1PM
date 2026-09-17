package com.drones.backend1PM.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequest(
        @NotNull(message = "Conta é obrigatória") Long contaId,
        String observacaoGeral,
        @NotBlank(message = "CEP é obrigatório") String cep,
        @NotBlank(message = "Logradouro é obrigatório") String logradouro,
        @NotBlank(message = "Número é obrigatório") String numero,
        @NotBlank(message = "Bairro é obrigatório") String bairro,
        @NotBlank(message = "Cidade é obrigatória") String cidade,
        @NotBlank(message = "Estado é obrigatório") String estado,
        @NotNull(message = "Pedido precisa ter pelo menos um item") List<@Valid ItemPedidoRequest> itens) {
    public record ItemPedidoRequest(
            @NotNull(message = "Produto é obrigatório") Long produtoId,
            @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser maior que zero") Integer quantidade,
            String observacaoItem) {
    }
}
