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
class CategoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
}