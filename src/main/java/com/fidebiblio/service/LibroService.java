package com.fidebiblio.service;

import com.fidebiblio.domain.Categoria;
import com.fidebiblio.domain.Libro;
import com.fidebiblio.repository.CategoriaRepository;
import com.fidebiblio.repository.LibroRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final FirebaseStorageService firebaseStorageService;

    public LibroService(LibroRepository libroRepository, CategoriaRepository categoriaRepository,
            FirebaseStorageService firebaseStorageService) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
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

    // Actualiza el estado físico del libro
    @Transactional
    public void actualizarEstadoFisico(Integer idLibro, String estadoFisico) {
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("El libro no existe"));
        libro.setEstadoFisico(estadoFisico);
        libroRepository.save(libro);
    }

    // Carga de libros desde un archivo CSV
    // Formato: isbn,titulo,autor,editorial,anioPublicacion,nombreCategoria,cantidadEjemplares
    @Transactional
    public List<String> cargarDesdeCSV(MultipartFile archivo) throws IOException {
        List<String> resultado = new ArrayList<>();
        int exitosos = 0;
        int fallidos = 0;

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            int numeroFila = 0;
            boolean primeraLinea = true;

            while ((linea = lector.readLine()) != null) {
                numeroFila++;
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                if (linea.isBlank()) {
                    continue;
                }

                String[] columnas = linea.split(",");

                try {
                    if (columnas.length < 7) {
                        throw new IllegalArgumentException("Faltan columnas (se esperan 7)");
                    }

                    String isbn = columnas[0].trim();
                    String titulo = columnas[1].trim();
                    String autor = columnas[2].trim();
                    String editorial = columnas[3].trim();
                    Integer anioPublicacion = Integer.parseInt(columnas[4].trim());
                    String nombreCategoria = columnas[5].trim();
                    Integer cantidadEjemplares = Integer.parseInt(columnas[6].trim());

                    if (libroRepository.existsByIsbn(isbn)) {
                        throw new IllegalArgumentException("El ISBN " + isbn + " ya existe");
                    }

                    Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "La categoría \"" + nombreCategoria + "\" no existe"));

                    Libro libro = new Libro();
                    libro.setIsbn(isbn);
                    libro.setTitulo(titulo);
                    libro.setAutor(autor);
                    libro.setEditorial(editorial);
                    libro.setAnioPublicacion(anioPublicacion);
                    libro.setCategoria(categoria);
                    libro.setCantidadEjemplares(cantidadEjemplares);
                    libro.setEjemplaresDisponibles(cantidadEjemplares);

                    libroRepository.save(libro);
                    exitosos++;

                } catch (Exception e) {
                    fallidos++;
                    resultado.add("Fila " + numeroFila + ": " + e.getMessage());
                }
            }
        }

        resultado.add(0, exitosos + " libro(s) cargado(s) satisfactoriamente, " + fallidos + " con error(es)");
        return resultado;
    }
}