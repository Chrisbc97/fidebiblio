package com.fidebiblio.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "La identificación es obligatoria")
    @Column(unique = true, nullable = false, length = 20)
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Column(name = "primer_apellido", nullable = false, length = 60)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 60)
    private String segundoApellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(unique = true, nullable = false, length = 100)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    // ADMINISTRADOR, BIBLIOTECARIO, ESTUDIANTE
    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false, length = 20)
    private String rol;

    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }

    public String getNombreCompleto() {
        return nombre + " " + primerApellido + (segundoApellido != null && !segundoApellido.isBlank() ? " " + segundoApellido : "");
    }
}