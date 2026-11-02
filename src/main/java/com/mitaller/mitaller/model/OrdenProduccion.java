package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ordenes_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccion extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroOrden; // Ej: "OP-2026-001"

    @Column(nullable = false)
    private Integer cantidadProgramada; // Ej: 500 unidades

    @Column(nullable = false)
    private Integer cantidadCortada; // Cuántas unidades realmente salieron del corte

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaEntregaEstimada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado = EstadoOrden.PLANIFICADA; // PLANIFICADA, EN_PROCESO, FINALIZADA, CANCELADA
}