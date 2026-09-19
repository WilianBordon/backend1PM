package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ContaRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public Conta salvar(ContaRequest request) {
        Conta conta = new Conta();
        conta.setNome(request.nome());
        conta.setEmail(request.email());
        conta.setSenha(request.senha());
        conta.setTelefone(request.telefone());
        return contaRepository.save(conta);
    }

    public List<Conta> listar() {

        log.info("Buscando todos as contas");

        List<Conta> produtos = contaRepository.findAll();

        log.info("Foram encontradas {} contas", produtos.size());

        return produtos;

    }

    public Optional<Conta> buscarPorId(Long id) {

        log.info("Buscando conta pelo ID: {}", id);

        Optional<Conta> conta = contaRepository.findById(id);

        if (conta.isPresent()) {
            log.info("Conta encontrado. ID: {}", id);
        } else {
            log.warn("Conta não encontrado. ID: {}", id);
        }

        return conta;

    }

    public Conta atualizar(Long id, ContaRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Conta não encontrada.");
                    return new RuntimeException("Conta não encontrada");
                });

        conta.setNome(request.nome());
        conta.setEmail(request.email());
        conta.setSenha(request.senha());
        conta.setTelefone(request.telefone());
        return contaRepository.save(conta);
    }

    public void excluir(Long id) {

        if (!contaRepository.existsById(id)) {
            log.warn("Tentativa de excluir conta inexistente. ID: {}", id);
            throw new RuntimeException("Conta não encontrado");
        }

        contaRepository.deleteById(id);

        log.info("Conta excluído com sucesso. ID: {}", id);

    }
}
