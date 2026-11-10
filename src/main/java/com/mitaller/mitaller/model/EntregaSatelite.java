package com.mitaller.mitaller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "entregas_satelite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregaSatelite extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "satelite_id", nullable = false)
    private TallerSatelite satelite;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_produccion_id")
    private OrdenProduccion ordenProduccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    @Column(nullable = false)
    private Integer cantidadPrendas; // Cantidad recibida y aprobada

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioPorPrenda; // Tarifa pactada con el satélite

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCobrado; // cantidadPrendas * precioPorPrenda

    @Column(nullable = false)
    private Boolean liquidado = false; // true si ya fue incluido en un pago semanal/quincenal

    @Column(nullable = false)
    private Boolean liquidada = false;

    public OrdenProduccion getOrdenProduccion() {
        return ordenProduccion;
    }

    public void setOrdenProduccion(OrdenProduccion ordenProduccion) {
        this.ordenProduccion = ordenProduccion;
    }

    private BigDecimal totalPagar;

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public Boolean getLiquidada() {
        return liquidada;
    }

    public void setLiquidada(Boolean liquidada) {
        this.liquidada = liquidada;
    }

    // En caso de que se use boolean primitivo (isLiquidada):
    public boolean isLiquidada() {
        return liquidada != null && liquidada;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquidacion_id")
    private LiquidacionSatelite liquidacion;

    public LiquidacionSatelite getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(LiquidacionSatelite liquidacion) {
        this.liquidacion = liquidacion;
    }
}