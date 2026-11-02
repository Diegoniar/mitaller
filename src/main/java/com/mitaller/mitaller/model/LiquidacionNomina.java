package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "liquidaciones_nomina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionNomina extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fechaInicio; // Ej: Lunes

    @Column(nullable = false)
    private LocalDate fechaFin; // Ej: Sábado

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDestajo; // Sumatoria automática de sus registros diarios

    @Column(precision = 12, scale = 2)
    private BigDecimal salarioBaseProporcional; // Por si el empleado tiene salario fijo

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal auxilioTransporte; // Por ley, si gana menos de 2 SMMLV

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deducciones; // 4% Salud y 4% Pensión (si aplica) o préstamos

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal provisiones; // El dinero "invisible" que el taller debe guardar (Primas, Cesantías, etc.)

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal netoAPagar; // El dinero real que se le transfiere a la cuenta
}