package com.fidebiblio.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion")
public class Configuracion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @NotBlank(message = "El atributo es obligatorio")
    @Column(name = "atributo", unique = true, nullable = false, length = 30)
    private String atributo;

    @NotBlank(message = "El valor es obligatorio")
    @Column(name = "valor", nullable = false, length = 150)
    private String valor;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
