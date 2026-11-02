package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas_destajo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TarifaDestajo extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fase_produccion_id", nullable = false)
    private FaseProduccion faseProduccion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorUnidad; // Cuánto se paga por hacer una unidad en esta fase
}