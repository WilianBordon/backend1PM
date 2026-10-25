package com.drones.backend1PM;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long cadastrarCategoria(String nome) throws Exception {
        String resposta = mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"%s"}
                                """.formatted(nome)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode categoria = objectMapper.readTree(resposta);
        return categoria.get("id").asLong();
    }

    @Test
    void deveCadastrarCategoria() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Canecas\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Canecas"));
    }

    @Test
    void deveListarCategorias() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Cestas personalizadas\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Cestas personalizadas")));
    }

    @Test
    void naoDeveCadastrarCategoriaSemNome() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        long id = cadastrarCategoria("Canecas");

        mockMvc.perform(get("/categorias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Canecas"));
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExistir() throws Exception {
        mockMvc.perform(get("/categorias/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        long id = cadastrarCategoria("Canecas");

        mockMvc.perform(put("/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Canecas personalizadas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome")
                        .value("Canecas personalizadas"));
    }

    @Test
    void deveExcluirCategoria() throws Exception {
        long id = cadastrarCategoria("Categoria para excluir");

        mockMvc.perform(delete("/categorias/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categorias/{id}", id))
                .andExpect(status().isNotFound());
    }
}