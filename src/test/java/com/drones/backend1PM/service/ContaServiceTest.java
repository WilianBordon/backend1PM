
package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ContaRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.repository.ContaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setId(1L);
        conta.setNome("João Silva");
        conta.setEmail("joao@email.com");
        conta.setSenha("senhaTeste");
        conta.setTelefone("45999999999");
    }


    @Test
    void deveSalvarContaComSucesso() {
        ContaRequest request = criarRequest(
                "Maria Silva",
                "maria@email.com",
                "senha123",
                "45988888888"
        );

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Conta resultado = contaService.salvar(request);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.getNome());
        assertEquals("maria@email.com", resultado.getEmail());
        assertEquals("senha123", resultado.getSenha());
        assertEquals("45988888888", resultado.getTelefone());

        verify(contaRepository).save(any(Conta.class));
    }


    @Test
    void deveListarContas() {
        Conta outraConta = new Conta();
        outraConta.setId(2L);
        outraConta.setNome("Maria Silva");
        outraConta.setEmail("maria@email.com");
        outraConta.setSenha("senha456");
        outraConta.setTelefone("45988888888");

        when(contaRepository.findAll())
                .thenReturn(List.of(conta, outraConta));

        List<Conta> resultado = contaService.listar();

        assertEquals(2, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        assertEquals("Maria Silva", resultado.get(1).getNome());

        verify(contaRepository).findAll();
    }


    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremContas() {
        when(contaRepository.findAll()).thenReturn(List.of());

        List<Conta> resultado = contaService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(contaRepository).findAll();
    }


    @Test
    void deveBuscarContaPorId() {
        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        Optional<Conta> resultado = contaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("joao@email.com", resultado.get().getEmail());

        verify(contaRepository).findById(1L);
    }


    @Test
    void deveRetornarOptionalVazioQuandoContaNaoExistir() {
        when(contaRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Conta> resultado = contaService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());

        verify(contaRepository).findById(99L);
    }


    @Test
    void deveAtualizarContaComSucesso() {
        ContaRequest request = criarRequest(
                "João Atualizado",
                "joao.novo@email.com",
                "novaSenha",
                "45977777777"
        );

        when(contaRepository.findById(1L))
                .thenReturn(Optional.of(conta));

        when(contaRepository.save(any(Conta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Conta resultado = contaService.atualizar(1L, request);

        assertEquals("João Atualizado", resultado.getNome());
        assertEquals("joao.novo@email.com", resultado.getEmail());
        assertEquals("novaSenha", resultado.getSenha());
        assertEquals("45977777777", resultado.getTelefone());

        verify(contaRepository).findById(1L);
        verify(contaRepository).save(conta);
    }


    @Test
    void deveLancarExcecaoAoAtualizarContaInexistente() {
        ContaRequest request = criarRequest(
                "João Atualizado",
                "joao.novo@email.com",
                "novaSenha",
                "45977777777"
        );

        when(contaRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> contaService.atualizar(99L, request)
        );

        assertEquals("Conta não encontrada", exception.getMessage());

        verify(contaRepository, never()).save(any(Conta.class));
    }


    @Test
    void deveExcluirContaComSucesso() {
        when(contaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> contaService.excluir(1L));

        verify(contaRepository).existsById(1L);
        verify(contaRepository).deleteById(1L);
    }


    @Test
    void deveLancarExcecaoAoExcluirContaInexistente() {
        when(contaRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> contaService.excluir(99L)
        );

        assertEquals("Conta não encontrado", exception.getMessage());

        verify(contaRepository, never()).deleteById(anyLong());
    }


    private ContaRequest criarRequest(
            String nome,
            String email,
            String senha,
            String telefone
    ) {
        return new ContaRequest(nome, email, senha, telefone);
    }
}

