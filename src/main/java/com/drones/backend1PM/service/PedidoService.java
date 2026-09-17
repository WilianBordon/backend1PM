package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.PedidoRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.entity.Pedido;
import com.drones.backend1PM.entity.PedidoItem;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.repository.ContaRepository;
import com.drones.backend1PM.repository.PedidoRepository;
import com.drones.backend1PM.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ContaRepository contaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public Pedido salvar(PedidoRequest request) {
        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        Pedido pedido = new Pedido();
        pedido.setConta(conta);
        pedido.setObservacaoGeral(request.observacaoGeral());
        pedido.setCep(request.cep());
        pedido.setLogradouro(request.logradouro());
        pedido.setNumero(request.numero());
        pedido.setBairro(request.bairro());
        pedido.setCidade(request.cidade());
        pedido.setEstado(request.estado());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus("PENDENTE");
        pedido.setItens(new ArrayList<>());

        Double total = 0.0;

        for (PedidoRequest.ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemRequest.produtoId()));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.quantidade() != null ? itemRequest.quantidade() : 1);
            item.setValorUnitario(produto.getPreco());
            item.setObservacaoItem(itemRequest.observacaoItem());

            pedido.getItens().add(item);
            total += produto.getPreco() * item.getQuantidade();
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }
}
