package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "registros_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroProduccion extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado; // El operario que hizo el trabajo

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fase_produccion_id", nullable = false)
    private FaseProduccion faseProduccion;

    @Column(nullable = false)
    private Integer cantidadRealizada;

    @Column(nullable = false)
    private LocalDate fechaTrabajo;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalGanado; // Se calcula automáticamente: cantidad * tarifa

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_produccion_id")
    private OrdenProduccion ordenProduccion;
}