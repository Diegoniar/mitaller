package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "fichas_insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichaInsumo extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal cantidadNecesaria; // Ej: 1.25 (metros de tela)

    @Column(precision = 5, scale = 2)
    private BigDecimal porcentajeMerma; // Ej: 0.05 (5% de desperdicio textil)
}