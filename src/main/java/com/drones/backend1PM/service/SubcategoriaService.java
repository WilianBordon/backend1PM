package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.SubcategoriaRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class SubcategoriaService {

    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    public Subcategoria salvar(SubcategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada.");
                    return new RuntimeException("Categoria não encontrada");
                });

        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setNome(request.nome());
        subcategoria.setCategoria(categoria);
        return subcategoriaRepository.save(subcategoria);
    }

    public List<Subcategoria> listar() {
        log.info("Buscando todos as subcategorias");

        List<Subcategoria> subcategorias = subcategoriaRepository.findAll();

        log.info("Foram encontrados {} subcategorias", subcategorias.size());

        return subcategorias;

    }

    public Optional<Subcategoria> buscarPorId(Long id) {

        log.info("Buscando subcategoria pelo ID: {}", id);

        Optional<Subcategoria> subcategoria = subcategoriaRepository.findById(id);

        if (subcategoria.isPresent()) {
            log.info("Subcategoria encontrado. ID: {}", id);
        } else {
            log.warn("Subcategoria não encontrado. ID: {}", id);
        }

        return subcategoria;
    }

    public Subcategoria atualizar(Long id, SubcategoriaRequest request) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Subcategoria não encontrada");
                    return new RuntimeException("Subcategoria não encontrada");
                });

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> {
                    log.warn("categoria não encontrada");
                    return new RuntimeException("Categoria não encontrada");
                });

        subcategoria.setNome(request.nome());
        subcategoria.setCategoria(categoria);
        return subcategoriaRepository.save(subcategoria);
    }

    public void excluir(Long id) {
        log.info("Iniciando exclusão da Subcategoria. ID: {}", id);

        if (!subcategoriaRepository.existsById(id)) {
            log.warn("Tentativa de excluir subcategoria inexistente. ID: {}", id);
            throw new RuntimeException("Subcategoria não encontrado");
        }

        subcategoriaRepository.deleteById(id);

        log.info("Subcategoria excluído com sucesso. ID: {}", id);

    }
}
