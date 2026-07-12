package com.fidebiblio.service;

import com.fidebiblio.domain.Categoria;
import com.fidebiblio.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean soloActivas) {
        return soloActivas ? categoriaRepository.findByActivoTrue() : categoriaRepository.findAll();
    }
}