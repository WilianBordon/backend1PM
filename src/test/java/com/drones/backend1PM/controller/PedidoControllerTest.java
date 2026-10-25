
package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.PedidoRequest;
import com.drones.backend1PM.entity.Pedido;
import com.drones.backend1PM.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    private PedidoRequest criarRequest() {
        return new PedidoRequest(
                1L,
                "Entregar à tarde",
                "85851-000",
                "Rua das Flores",
                "123",
                "Centro",
                "Foz do Iguaçu",
                "PR",
                List.of(
                        new PedidoRequest.ItemPedidoRequest(
                                1L, 2, "Embalagem para presente"
                        )
                )
        );
    }

    private Pedido criarPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setObservacaoGeral("Entregar à tarde");
        pedido.setCep("85851-000");
        pedido.setLogradouro("Rua das Flores");
        pedido.setNumero("123");
        pedido.setBairro("Centro");
        pedido.setCidade("Foz do Iguaçu");
        pedido.setEstado("PR");
        pedido.setStatus("PENDENTE");
        pedido.setTotal(100.0);
        return pedido;
    }

    @Test
    void deveCadastrarPedido() throws Exception {
        when(pedidoService.salvar(any(PedidoRequest.class)))
                .thenReturn(criarPedido());

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.observacaoGeral")
                        .value("Entregar à tarde"))
                .andExpect(jsonPath("$.cep").value("85851-000"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.total").value(100.0));

        verify(pedidoService).salvar(any(PedidoRequest.class));
    }

    @Test
    void deveRetornar400QuandoContaNaoForInformada()
            throws Exception {
        String json = """
                {
                    "contaId": null,
                    "observacaoGeral": "Teste",
                    "cep": "85851-000",
                    "itens": [
                        {
                            "produtoId": 1,
                            "quantidade": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    @Test
    void deveRetornar400QuandoCepNaoForInformado()
            throws Exception {
        String json = """
                {
                    "contaId": 1,
                    "cep": "",
                    "itens": [
                        {
                            "produtoId": 1,
                            "quantidade": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    @Test
    void deveRetornar400QuandoItemTiverQuantidadeInvalida()
            throws Exception {
        String json = """
                {
                    "contaId": 1,
                    "cep": "85851-000",
                    "itens": [
                        {
                            "produtoId": 1,
                            "quantidade": 0
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    @Test
    void deveListarPedidos() throws Exception {
        when(pedidoService.listar())
                .thenReturn(List.of(criarPedido()));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cep").value("85851-000"));

        verify(pedidoService).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremPedidos()
            throws Exception {
        when(pedidoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(pedidoService).listar();
    }

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        when(pedidoService.buscarPorId(1L))
                .thenReturn(Optional.of(criarPedido()));

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(pedidoService).buscarPorId(1L);
    }

    @Test
    void deveRetornar404QuandoPedidoNaoForEncontrado()
            throws Exception {
        when(pedidoService.buscarPorId(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/99"))
                .andExpect(status().isNotFound());

        verify(pedidoService).buscarPorId(99L);
    }

    @Test
    void deveExcluirPedido() throws Exception {
        doNothing().when(pedidoService).excluir(1L);

        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService).excluir(1L);
    }
}
