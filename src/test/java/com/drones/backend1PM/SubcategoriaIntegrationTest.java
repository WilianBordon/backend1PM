package com.drones.backend1PM;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubcategoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarSubcategoria() throws Exception {
        String categoriaJson = mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Canecas\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long categoriaId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(categoriaJson)
                .get("id")
                .asLong();

        mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Canecas personalizadas\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Canecas personalizadas"));
    }

    @Test
    void deveListarSubcategorias() throws Exception {
        String categoriaJson = mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Cestas\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long categoriaId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(categoriaJson)
                .get("id")
                .asLong();

        mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Cestas de presente\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/subcategorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Cestas de presente")));
    }

    @Test
    void naoDeveCadastrarSubcategoriaSemNome() throws Exception {
        mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"\",\"categoriaId\":1}"))
                .andExpect(status().isBadRequest());
    }
}
