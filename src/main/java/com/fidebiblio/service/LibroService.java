package com.fidebiblio.service;

import com.fidebiblio.domain.Libro;
import com.fidebiblio.repository.LibroRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final FirebaseStorageService firebaseStorageService;

    public LibroService(LibroRepository libroRepository, FirebaseStorageService firebaseStorageService) {
        this.libroRepository = libroRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Libro> getLibros(boolean soloActivos) {
        return soloActivos ? libroRepository.findByActivoTrue() : libroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Libro> getLibro(Integer idLibro) {
        return libroRepository.findById(idLibro);
    }

    // Buscar libro por título
    @Transactional(readOnly = true)
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCaseAndActivoTrue(titulo);
    }

    // Buscar libro por autor
    @Transactional(readOnly = true)
    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCaseAndActivoTrue(autor);
    }

    // Buscar libros por categoría
    @Transactional(readOnly = true)
    public List<Libro> buscarPorCategoria(Integer idCategoria) {
        return libroRepository.findByCategoria_IdCategoriaAndActivoTrue(idCategoria);
    }

    // Registrar un libro
    @Transactional
    public void registrar(Libro libro, MultipartFile imagenFile) {
        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro registrado con ese ISBN");
        }
        libro.setEjemplaresDisponibles(libro.getCantidadEjemplares());
        libro = libroRepository.save(libro);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(imagenFile, "libro", libro.getIdLibro());
                libro.setRutaImagen(rutaImagen);
                libroRepository.save(libro);
            } catch (IOException e) {
            }
        }
    }

    // Actualizar un libro
    @Transactional
    public void actualizar(Libro libro, MultipartFile imagenFile) {
        Libro existente = libroRepository.findById(libro.getIdLibro())
                .orElseThrow(() -> new IllegalArgumentException("El libro no existe"));

        existente.setTitulo(libro.getTitulo());
        existente.setAutor(libro.getAutor());
        existente.setEditorial(libro.getEditorial());
        existente.setAnioPublicacion(libro.getAnioPublicacion());
        existente.setCategoria(libro.getCategoria());
        existente.setUbicacionFisica(libro.getUbicacionFisica());

        int diferencia = libro.getCantidadEjemplares() - existente.getCantidadEjemplares();
        existente.setCantidadEjemplares(libro.getCantidadEjemplares());
        existente.setEjemplaresDisponibles(Math.max(0, existente.getEjemplaresDisponibles() + diferencia));

        libroRepository.save(existente);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(imagenFile, "libro", existente.getIdLibro());
                existente.setRutaImagen(rutaImagen);
                libroRepository.save(existente);
            } catch (IOException e) {
            }
        }
    }

    // Eliminar o desactivar un libro
    @Transactional
    public void eliminar(Integer idLibro) {
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("El libro no existe"));
        libro.setActivo(false);
        libroRepository.save(libro);
    }
}