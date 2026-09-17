package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.ContaRequest;
import com.drones.backend1PM.entity.Conta;
import com.drones.backend1PM.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return contaRepository.findAll();
    }

    public Optional<Conta> buscarPorId(Long id) {
        return contaRepository.findById(id);
    }

    public Conta atualizar(Long id, ContaRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        conta.setNome(request.nome());
        conta.setEmail(request.email());
        conta.setSenha(request.senha());
        conta.setTelefone(request.telefone());
        return contaRepository.save(conta);
    }

    public void excluir(Long id) {
        contaRepository.deleteById(id);
    }
}
