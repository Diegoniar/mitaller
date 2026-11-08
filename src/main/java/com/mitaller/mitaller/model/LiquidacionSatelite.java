package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "liquidaciones_satelite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionSatelite extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "satelite_id", nullable = false)
    private TallerSatelite satelite;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer totalPrendasConfeccionadas;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalBruto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal retenciones; // Retefuente si aplica o descuentos por prendas defectuosas

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netoAPagar;

    @Column(length = 250)
    private String observaciones;
}