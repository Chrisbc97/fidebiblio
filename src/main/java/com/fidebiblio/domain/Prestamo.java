package com.fidebiblio.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "prestamo")
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Integer idPrestamo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "fecha_prestamo", updatable = false)
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    // ACTIVO, DEVUELTO, VENCIDO
    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO";

    @PrePersist
    public void prePersist() {
        if (fechaPrestamo == null) {
            fechaPrestamo = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "ACTIVO";
        }
    }
}