package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "empleados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empleado extends EntidadBase{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa; // Relaciona al empleado con su respectivo taller

    @Column(nullable = false, length = 100)
    private String nombreCompleto;

    @Column(nullable = false, length = 20)
    private String documentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoVinculacion tipoVinculacion;

    @Column(length = 50)
    private String especialidad; // Ej: Cortador, Fileteador, Empacador, Administrador

    // Base salarial para administrativos. Para destajo puede ser 0.
    @Column(precision = 12, scale = 2)
    private BigDecimal salarioBase;

    @Column(nullable = false)
    private boolean activo = true;

    // Los tipos de contrato comunes en los talleres
    public enum TipoVinculacion {
        DESTAJO,
        SALARIO_FIJO,
        PRESTACION_SERVICIOS
    }
}
