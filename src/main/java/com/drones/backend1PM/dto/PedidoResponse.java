package com.drones.backend1PM.dto;

import com.drones.backend1PM.entity.Pedido;
import com.drones.backend1PM.entity.PedidoItem;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long contaId,
        String nomeCliente,
        String observacaoGeral,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String estado,
        String status,
        String dataPedido,
        Double total,
        List<ItemPedidoResponse> itens) {
    public static PedidoResponse de(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        List<ItemPedidoResponse> itens = pedido.getItens() == null
                ? List.of()
                : pedido.getItens().stream().map(ItemPedidoResponse::de).toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getConta() != null ? pedido.getConta().getId() : null,
                pedido.getConta() != null ? pedido.getConta().getNome() : null,
                pedido.getObservacaoGeral(),
                pedido.getCep(),
                pedido.getLogradouro(),
                pedido.getNumero(),
                pedido.getBairro(),
                pedido.getCidade(),
                pedido.getEstado(),
                pedido.getStatus(),
                pedido.getDataPedido() != null
                        ? pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : null,
                pedido.getTotal(),
                itens);
    }

    public record ItemPedidoResponse(
            Long id,
            Long produtoId,
            String nomeProduto,
            Integer quantidade,
            Double valorUnitario,
            String observacaoItem) {
        public static ItemPedidoResponse de(PedidoItem item) {
            if (item == null) {
                return null;
            }

            return new ItemPedidoResponse(
                    item.getId(),
                    item.getProduto() != null ? item.getProduto().getId() : null,
                    item.getProduto() != null ? item.getProduto().getNome() : null,
                    item.getQuantidade(),
                    item.getValorUnitario(),
                    item.getObservacaoItem());
        }
    }
}
