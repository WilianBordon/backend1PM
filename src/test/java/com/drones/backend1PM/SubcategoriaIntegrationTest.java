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

    @Test
    void deveBuscarSubcategoriaPorId() throws Exception {
        String categoriaJson = mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Categoria para busca\"}"))
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

        String subcategoriaJson = mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Subcategoria para busca\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long subcategoriaId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(subcategoriaJson)
                .get("id")
                .asLong();

        mockMvc.perform(get("/subcategorias/" + subcategoriaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subcategoriaId))
                .andExpect(jsonPath("$.nome").value("Subcategoria para busca"));
    }

    @Test
    void deveRetornar404QuandoSubcategoriaNaoExistir() throws Exception {
        mockMvc.perform(get("/subcategorias/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarSubcategoria() throws Exception {
        String categoriaJson = mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Categoria para atualizar\"}"))
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

        String subcategoriaJson = mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Nome antigo\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long subcategoriaId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(subcategoriaJson)
                .get("id")
                .asLong();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/subcategorias/" + subcategoriaId)
                        .contentType("application/json")
                        .content("{\"nome\":\"Nome atualizado\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subcategoriaId))
                .andExpect(jsonPath("$.nome").value("Nome atualizado"));
    }

    @Test
    void deveExcluirSubcategoria() throws Exception {
        String categoriaJson = mockMvc.perform(post("/categorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Categoria para excluir\"}"))
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

        String subcategoriaJson = mockMvc.perform(post("/subcategorias")
                        .contentType("application/json")
                        .content("{\"nome\":\"Subcategoria para excluir\",\"categoriaId\":"
                                + categoriaId + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long subcategoriaId = com.fasterxml.jackson.databind.json.JsonMapper
                .builder()
                .build()
                .readTree(subcategoriaJson)
                .get("id")
                .asLong();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/subcategorias/" + subcategoriaId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/subcategorias/" + subcategoriaId))
                .andExpect(status().isNotFound());
    }

}
