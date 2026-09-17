package com.drones.backend1PM.service;

import com.drones.backend1PM.dto.SubcategoriaRequest;
import com.drones.backend1PM.entity.Categoria;
import com.drones.backend1PM.entity.Subcategoria;
import com.drones.backend1PM.repository.CategoriaRepository;
import com.drones.backend1PM.repository.SubcategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubcategoriaService {

    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;

    public Subcategoria salvar(SubcategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Subcategoria subcategoria = new Subcategoria();
        subcategoria.setNome(request.nome());
        subcategoria.setCategoria(categoria);
        return subcategoriaRepository.save(subcategoria);
    }

    public List<Subcategoria> listar() {
        return subcategoriaRepository.findAll();
    }

    public Optional<Subcategoria> buscarPorId(Long id) {
        return subcategoriaRepository.findById(id);
    }

    public Subcategoria atualizar(Long id, SubcategoriaRequest request) {
        Subcategoria subcategoria = subcategoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        subcategoria.setNome(request.nome());
        subcategoria.setCategoria(categoria);
        return subcategoriaRepository.save(subcategoria);
    }

    public void excluir(Long id) {
        subcategoriaRepository.deleteById(id);
    }
}
