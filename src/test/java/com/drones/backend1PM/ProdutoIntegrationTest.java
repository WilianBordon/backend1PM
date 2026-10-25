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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    private long cadastrarSubcategoria(long categoriaId) throws Exception {
        String resposta = mockMvc.perform(post("/subcategorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Canecas personalizadas",
                                    "categoriaId": %d
                                }
                                """.formatted(categoriaId)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resposta).get("id").asLong();
    }

    private String produtoJsonComSubcategoria(
            long categoriaId, long subcategoriaId) {
        return """
                {
                    "nome": "Caneca personalizada",
                    "descricao": "Caneca com nome",
                    "preco": 35.90,
                    "quantidade": 10,
                    "imagemPrincipal": "caneca.jpg",
                    "ativo": true,
                    "destaque": false,
                    "categoriaId": %d,
                    "subcategoriaId": %d
                }
                """.formatted(categoriaId, subcategoriaId);
    }

    private long cadastrarProduto(long categoriaId) throws Exception {
        String resposta = mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJson(categoriaId)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resposta).get("id").asLong();
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

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        long categoriaId = cadastrarCategoria();
        long produtoId = cadastrarProduto(categoriaId);

        mockMvc.perform(get("/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(produtoId))
                .andExpect(jsonPath("$.nome").value("Caneca personalizada"));
    }

    @Test
    void deveRetornar404QuandoProdutoNaoExistir() throws Exception {
        mockMvc.perform(get("/produtos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarProdutosPorNome() throws Exception {
        long categoriaId = cadastrarCategoria();
        cadastrarProduto(categoriaId);

        mockMvc.perform(get("/produtos/buscarpornome")
                        .param("nome", "Caneca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Caneca personalizada")));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        long categoriaId = cadastrarCategoria();
        long produtoId = cadastrarProduto(categoriaId);

        String produtoAtualizado = """
                {
                    "nome": "Caneca atualizada",
                    "descricao": "Descrição atualizada",
                    "preco": 45.90,
                    "quantidade": 20,
                    "imagemPrincipal": "caneca-nova.jpg",
                    "ativo": true,
                    "destaque": true,
                    "categoriaId": %d
                }
                """.formatted(categoriaId);

        mockMvc.perform(put("/produtos/" + produtoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(produtoId))
                .andExpect(jsonPath("$.nome").value("Caneca atualizada"))
                .andExpect(jsonPath("$.preco").value(45.90))
                .andExpect(jsonPath("$.quantidade").value(20));
    }

    @Test
    void deveListarProdutosPorCategoria() throws Exception {
        long categoriaId = cadastrarCategoria();
        cadastrarProduto(categoriaId);

        mockMvc.perform(get("/produtos/categoria/" + categoriaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Caneca personalizada")));
    }

    @Test
    void deveListarProdutosPorSubcategoria() throws Exception {
        long categoriaId = cadastrarCategoria();
        long subcategoriaId = cadastrarSubcategoria(categoriaId);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoJsonComSubcategoria(
                                categoriaId, subcategoriaId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/produtos/subcategoria/" + subcategoriaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome",
                        hasItem("Caneca personalizada")));
    }

    @Test
    void deveExcluirProduto() throws Exception {
        long categoriaId = cadastrarCategoria();
        long produtoId = cadastrarProduto(categoriaId);

        mockMvc.perform(delete("/produtos/" + produtoId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/produtos/" + produtoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveCadastrarProdutoComPrecoNegativo() throws Exception {
        long categoriaId = cadastrarCategoria();

        String produtoInvalido = """
                {
                    "nome": "Produto inválido",
                    "descricao": "Teste de preço",
                    "preco": -10.0,
                    "quantidade": 10,
                    "imagemPrincipal": "produto.jpg",
                    "categoriaId": %d
                }
                """.formatted(categoriaId);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarProdutoComQuantidadeZero() throws Exception {
        long categoriaId = cadastrarCategoria();

        String produtoInvalido = """
                {
                    "nome": "Produto inválido",
                    "descricao": "Teste de quantidade",
                    "preco": 10.0,
                    "quantidade": 0,
                    "imagemPrincipal": "produto.jpg",
                    "categoriaId": %d
                }
                """.formatted(categoriaId);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoInvalido))
                .andExpect(status().isBadRequest());
    }
}
