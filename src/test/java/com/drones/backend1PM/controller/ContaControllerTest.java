
package com.drones.backend1PM.controller;

import com.drones.backend1PM.dto.ContaRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.service.ContaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContaService contaService;

    private Conta criarConta() {
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setNome("Guilherme");
        conta.setEmail("guilherme@email.com");
        conta.setSenha("senha123");
        conta.setTelefone("45999999999");
        return conta;
    }

    private ContaRequest criarRequest() {
        return new ContaRequest(
                "Guilherme",
                "guilherme@email.com",
                "senha123",
                "45999999999"
        );
    }

    @Test
    void deveCadastrarConta() throws Exception {
        when(contaService.salvar(any(ContaRequest.class)))
                .thenReturn(criarConta());

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Guilherme"))
                .andExpect(jsonPath("$.email").value("guilherme@email.com"));

        verify(contaService).salvar(any(ContaRequest.class));
    }

    @Test
    void deveListarContas() throws Exception {
        when(contaService.listar())
                .thenReturn(List.of(criarConta()));

        mockMvc.perform(get("/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Guilherme"))
                .andExpect(jsonPath("$[0].email").value("guilherme@email.com"));

        verify(contaService).listar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremContas()
            throws Exception {
        when(contaService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(contaService).listar();
    }

    @Test
    void deveBuscarContaPorId() throws Exception {
        when(contaService.buscarPorId(1L))
                .thenReturn(Optional.of(criarConta()));

        mockMvc.perform(get("/contas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Guilherme"))
                .andExpect(jsonPath("$.email").value("guilherme@email.com"));

        verify(contaService).buscarPorId(1L);
    }

    @Test
    void deveRetornar404QuandoContaNaoForEncontrada()
            throws Exception {
        when(contaService.buscarPorId(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/contas/99"))
                .andExpect(status().isNotFound());

        verify(contaService).buscarPorId(99L);
    }

    @Test
    void deveAtualizarConta() throws Exception {
        Conta atualizada = criarConta();
        atualizada.setNome("Guilherme Atualizado");

        when(contaService.atualizar(
                eq(1L), any(ContaRequest.class)))
                .thenReturn(atualizada);

        ContaRequest request = new ContaRequest(
                "Guilherme Atualizado",
                "guilherme@email.com",
                "senha123",
                "45999999999"
        );

        mockMvc.perform(put("/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Guilherme Atualizado"));

        verify(contaService)
                .atualizar(eq(1L), any(ContaRequest.class));
    }

    @Test
    void deveExcluirConta() throws Exception {
        doNothing().when(contaService).excluir(1L);

        mockMvc.perform(delete("/contas/1"))
                .andExpect(status().isNoContent());

        verify(contaService).excluir(1L);
    }
}
