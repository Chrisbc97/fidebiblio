package com.fidebiblio.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;


@Data
@Entity
@Table(name = "libro")
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Integer idLibro;

    @NotBlank(message = "El ISBN es obligatorio")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Column(nullable = false, length = 120)
    private String autor;

    @Column(length = 100)
    private String editorial;

    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @NotNull(message = "La cantidad de ejemplares es obligatoria")
    @Column(name = "cantidad_ejemplares")
    private Integer cantidadEjemplares = 0;

    @Column(name = "ejemplares_disponibles")
    private Integer ejemplaresDisponibles = 0;

    @Column(name = "ubicacion_fisica", length = 60)
    private String ubicacionFisica;

    @Column(name = "ruta_imagen")
    private String rutaImagen;

    private Boolean activo = true;
}
