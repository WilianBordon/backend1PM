package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.CategoriaRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Categoria salvar(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        log.info("Categoria cadastrada com sucesso.");
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listar() {

        log.info("Buscando todos as categorias");

        List<Categoria> categorias = categoriaRepository.findAll();

        log.info("Foram encontrados {} categorias", categorias.size());

        return categorias;

    }

    public Optional<Categoria> buscarPorId(Long id) {

        log.info("Buscando categoria pelo ID: {}", id);

        Optional<Categoria> categoria = categoriaRepository.findById(id);

        if (categoria.isPresent()) {
            log.info("Categoria encontrado. ID: {}", id);
        } else {
            log.warn("Categoria não encontrado. ID: {}", id);
        }

        return categoria;

    }

    public Categoria atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Conta não encontrada.");
                   return new RuntimeException("Categoria não encontrada");
                });
        categoria.setNome(request.nome());
        return categoriaRepository.save(categoria);
    }

    public void excluir(Long id) {

        if (!categoriaRepository.existsById(id)) {
            log.warn("Tentativa de excluir Categoria inexistente. ID: {}", id);
            throw new RuntimeException("Categoria não encontrado");
        }

        categoriaRepository.deleteById(id);

        log.info("Categoria excluída com sucesso. ID: {}", id);

    }
}
