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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long cadastrarCategoria() throws Exception {
        String resposta = mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Canecas\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode categoria = objectMapper.readTree(resposta);
        return categoria.get("id").asLong();
    }

    private String produtoJson(long categoriaId) {
        return """
                {
                    "nome": "Caneca personalizada",
                    "descricao": "Caneca com nome",
                    "preco": 35.90,
                    "quantidade": 10,
                    "imagemPrincipal": "caneca.jpg",
                    "ativo": true,
                    "destaque": false,
                    "categoriaId": %d
                }
                """.formatted(categoriaId);
    }

    @Test
    void deveCadastrarProduto() throws Exception {
        long categoriaId = cadastrarCategoria();

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson(categoriaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Caneca personalizada"));
    }

    @Test
    void deveListarProdutos() throws Exception {
        long categoriaId = cadastrarCategoria();

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson(categoriaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Caneca personalizada")));
    }

    @Test
    void naoDeveCadastrarProdutoSemNome() throws Exception {
        long categoriaId = cadastrarCategoria();

        String produtoInvalido = """
                {
                    "nome": "",
                    "descricao": "Produto para teste",
                    "preco": 35.90,
                    "quantidade": 10,
                    "imagemPrincipal": "caneca.jpg",
                    "categoriaId": %d
                }
                """.formatted(categoriaId);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoInvalido))
                .andExpect(status().isBadRequest());
    }
}
