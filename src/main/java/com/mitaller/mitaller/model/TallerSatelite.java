package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "talleres_satelite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TallerSatelite extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 120)
    private String nombreTaller; // Ej: "Satélite Doña Carmen"

    @Column(nullable = false, length = 120)
    private String nombreEncargado;

    @Column(length = 20)
    private String telefono;

    @Column(length = 50)
    private String nitOIdentificacion;

    @Column(length = 150)
    private String direccion;

    @Column(nullable = false)
    private Boolean activo = true;
}