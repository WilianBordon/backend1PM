
package com.drones.backend1PM.service;

import com.drones.backend1PM.client.ViaCepClient;
import com.drones.backend1PM.dto.PedidoRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.entity.Pedido;
import com.drones.backend1PM.entity.PedidoItem;
import com.drones.backend1PM.entity.Produto;
import com.drones.backend1PM.repository.ContaRepository;
import com.drones.backend1PM.repository.PedidoRepository;
import com.drones.backend1PM.repository.ProdutoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private PedidoService pedidoService;

    private Conta conta;
    private Produto produto;
    private Produto segundoProduto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setId(1L);
        conta.setNome("Cliente Teste");
        conta.setEmail("cliente@teste.com");
        conta.setSenha("senha");
        conta.setTelefone("45999999999");

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Caneca");
        produto.setPreco(50.0);
        produto.setQuantidade(10);

        segundoProduto = new Produto();
        segundoProduto.setId(20L);
        segundoProduto.setNome("Camiseta");
        segundoProduto.setPreco(80.0);
        segundoProduto.setQuantidade(5);

        pedido = new Pedido();
        pedido.setId(100L);
        pedido.setConta(conta);
        pedido.setTotal(100.0);
        pedido.setStatus("PENDENTE");
        pedido.setCep("85801000");
        pedido.setLogradouro("Rua Brasil");
        pedido.setNumero("123");
        pedido.setBairro("Centro");
        pedido.setCidade("Foz do Iguacu");
        pedido.setEstado("PR");
    }

    private PedidoRequest criarRequestComEnderecoIncompleto() {
        return new PedidoRequest(
                1L,
                "Pedido de teste",
                "85801-000",
                null,
                "123",
                null,
                null,
                null,
                List.of(
                        new PedidoRequest.ItemPedidoRequest(
                                10L,
                                1,
                                "Item de teste"
                        )
                )
        );
    }

    private ViaCepClient.EnderecoViaCepResponse criarRespostaViaCepComErro() {
        return new ViaCepClient.EnderecoViaCepResponse(
                "85801000",
                null,
                null,
                null,
                null,
                null,
                true
        );
    }

    private PedidoRequest criarRequest(
            String cep,
            String logradouro,
            String bairro,
            String cidade,
            String estado,
            List<PedidoRequest.ItemPedidoRequest> itens) {

        return new PedidoRequest(
                1L,
                "Pedido de teste",
                cep,
                logradouro,
                "123",
                bairro,
                cidade,
                estado,
                itens
        );
    }

    private PedidoRequest.ItemPedidoRequest criarItem(
            Long produtoId, Integer quantidade, String observacao) {

        return new PedidoRequest.ItemPedidoRequest(
                produtoId,
                quantidade,
                observacao
        );
    }

    private PedidoRequest criarRequestEnderecoCompleto(
            List<PedidoRequest.ItemPedidoRequest> itens) {

        return criarRequest(
                "85801-000",
                "Rua Brasil",
                "Centro",
                "Foz do Iguacu",
                "PR",
                itens
        );
    }

    private ViaCepClient.EnderecoViaCepResponse enderecoViaCep() {
        return new ViaCepClient.EnderecoViaCepResponse(
                "85801-000",
                "Avenida Brasil",
                "Centro",
                "Foz do Iguacu",
                "PR",
                "",
                false
        );
    }

    private void prepararContaEProduto() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(produtoRepository.findById(10L))
                .thenReturn(Optional.of(produto));
    }


    @Test
    void deveSalvarPedidoComSucesso() {
        prepararContaEProduto();

        PedidoRequest request = criarRequestEnderecoCompleto(
                List.of(criarItem(10L, 2, "Estampa azul"))
        );

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> {
                    Pedido p = invocation.getArgument(0);
                    p.setId(100L);
                    return p;
                });

        Pedido resultado = pedidoService.salvar(request);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertSame(conta, resultado.getConta());
        assertEquals("PENDENTE", resultado.getStatus());
        assertEquals("85801000", resultado.getCep());
        assertEquals("Rua Brasil", resultado.getLogradouro());
        assertEquals(100.0, resultado.getTotal(), 0.001);
        assertNotNull(resultado.getDataPedido());

        assertEquals(1, resultado.getItens().size());

        PedidoItem item = resultado.getItens().get(0);
        assertSame(resultado, item.getPedido());
        assertSame(produto, item.getProduto());
        assertEquals(2, item.getQuantidade());
        assertEquals(50.0, item.getValorUnitario(), 0.001);
        assertEquals("Estampa azul", item.getObservacaoItem());

        verify(pedidoRepository).save(any(Pedido.class));
        verify(viaCepClient, never()).buscarPorCep(anyString());
    }

    @Test
    void deveCalcularTotalComVariosItens() {
        prepararContaEProduto();

        when(produtoRepository.findById(20L))
                .thenReturn(Optional.of(segundoProduto));

        PedidoRequest request = criarRequestEnderecoCompleto(
                List.of(
                        criarItem(10L, 2, null),
                        criarItem(20L, 3, "Tamanho G")
                )
        );

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.salvar(request);

        // (50 * 2) + (80 * 3) = 340
        assertEquals(340.0, resultado.getTotal(), 0.001);
        assertEquals(2, resultado.getItens().size());

        verify(produtoRepository).findById(10L);
        verify(produtoRepository).findById(20L);
    }

    @Test
    void deveUsarQuantidadeUmQuandoQuantidadeForNula() {
        prepararContaEProduto();

        PedidoRequest request = criarRequestEnderecoCompleto(
                List.of(criarItem(10L, null, null))
        );

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.salvar(request);

        assertEquals(1, resultado.getItens().get(0).getQuantidade());
        assertEquals(50.0, resultado.getTotal(), 0.001);
    }

    @Test
    void deveLancarExcecaoQuandoContaNaoExiste() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.empty());

        PedidoRequest request = criarRequestEnderecoCompleto(
                List.of(criarItem(10L, 1, null))
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals("Conta não encontrada", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(produtoRepository, never()).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        PedidoRequest request = criarRequestEnderecoCompleto(
                List.of(criarItem(99L, 1, null))
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals("Produto não encontrado: 99", exception.getMessage());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    @Test
    void deveNormalizarCepRemovendoHifenEEspacos() {
        prepararContaEProduto();

        PedidoRequest request = criarRequest(
                " 85801-000 ",
                "Rua Brasil",
                "Centro",
                "Foz do Iguacu",
                "PR",
                List.of(criarItem(10L, 1, null))
        );

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.salvar(request);

        assertEquals("85801000", resultado.getCep());
        verify(viaCepClient, never()).buscarPorCep(anyString());
    }

    @Test
    void devePreencherEnderecoComDadosDoViaCep() {
        prepararContaEProduto();

        PedidoRequest request = criarRequest(
                "85801-000",
                null,
                null,
                null,
                null,
                List.of(criarItem(10L, 1, null))
        );

        when(viaCepClient.buscarPorCep("85801000"))
                .thenReturn(enderecoViaCep());

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.salvar(request);

        assertEquals("Avenida Brasil", resultado.getLogradouro());
        assertEquals("Centro", resultado.getBairro());
        assertEquals("Foz do Iguacu", resultado.getCidade());
        assertEquals("PR", resultado.getEstado());

        verify(viaCepClient).buscarPorCep("85801000");
    }

    @Test
    void devePreservarCamposInformadosEPreencherSomenteOsFaltantes() {
        prepararContaEProduto();

        PedidoRequest request = criarRequest(
                "85801-000",
                "Minha Rua",
                null,
                "Minha Cidade",
                null,
                List.of(criarItem(10L, 1, null))
        );

        when(viaCepClient.buscarPorCep("85801000"))
                .thenReturn(enderecoViaCep());

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.salvar(request);

        assertEquals("Minha Rua", resultado.getLogradouro());
        assertEquals("Centro", resultado.getBairro());
        assertEquals("Minha Cidade", resultado.getCidade());
        assertEquals("PR", resultado.getEstado());
    }

    @Test
    void deveLancarExcecaoQuandoCepForNulo() {
        PedidoRequest request = criarRequest(
                null,
                "Rua Brasil",
                "Centro",
                "Foz do Iguacu",
                "PR",
                List.of(criarItem(10L, 1, null))
        );

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals(
                "CEP é obrigatório para o pedido",
                exception.getMessage()
        );

        verify(viaCepClient, never()).buscarPorCep(anyString());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveLancarExcecaoQuandoCepForVazio() {
        PedidoRequest request = criarRequest(
                "   ",
                "Rua Brasil",
                "Centro",
                "Foz do Iguacu",
                "PR",
                List.of(criarItem(10L, 1, null))
        );

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals(
                "CEP é obrigatório para o pedido",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoViaCepRetornarNulo() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(viaCepClient.buscarPorCep("85801000"))
                .thenReturn(null);

        PedidoRequest request = criarRequestComEnderecoIncompleto();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals(
                "CEP não encontrado ou inválido",
                exception.getMessage()
        );

        verify(produtoRepository, never()).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoViaCepIndicarErro() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(viaCepClient.buscarPorCep("85801000"))
                .thenReturn(criarRespostaViaCepComErro());

        PedidoRequest request = criarRequestComEnderecoIncompleto();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.salvar(request)
        );

        assertEquals(
                "CEP não encontrado ou inválido",
                exception.getMessage()
        );

        verify(produtoRepository, never()).findById(anyLong());
    }


    @Test
    void deveListarTodosOsPedidos() {
        when(pedidoRepository.findAll())
                .thenReturn(List.of(pedido));

        List<Pedido> resultado = pedidoService.listar();

        assertEquals(1, resultado.size());
        assertSame(pedido, resultado.get(0));
        verify(pedidoRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistemPedidos() {
        when(pedidoRepository.findAll())
                .thenReturn(List.of());

        List<Pedido> resultado = pedidoService.listar();

        assertTrue(resultado.isEmpty());
    }


    @Test
    void deveEncontrarPedidoPorId() {
        when(pedidoRepository.findById(100L))
                .thenReturn(Optional.of(pedido));

        Optional<Pedido> resultado = pedidoService.buscarPorId(100L);

        assertTrue(resultado.isPresent());
        assertSame(pedido, resultado.get());
    }

    @Test
    void deveRetornarOptionalVazioQuandoPedidoNaoExiste() {
        when(pedidoRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Pedido> resultado = pedidoService.buscarPorId(999L);

        assertTrue(resultado.isEmpty());
    }


    @Test
    void deveExcluirPedidoExistente() {
        when(pedidoRepository.existsById(100L))
                .thenReturn(true);

        assertDoesNotThrow(() -> pedidoService.excluir(100L));

        verify(pedidoRepository).deleteById(100L);
    }

    @Test
    void deveLancarExcecaoAoExcluirPedidoInexistente() {
        when(pedidoRepository.existsById(999L))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pedidoService.excluir(999L)
        );

        assertEquals("Pedido não encontrado", exception.getMessage());

        verify(pedidoRepository, never()).deleteById(anyLong());
    }
}
