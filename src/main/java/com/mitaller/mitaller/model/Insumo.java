package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Insumo extends EntidadBase{
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nombre; // Ej: "Hilo de coser calibre 120", "Tela Denim 14 oz"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UnidadMedida unidadMedida;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoUnitario; // Cuánto vale un metro o una unidad

    @Column(nullable = false)
    private boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoInsumo tipoInsumo = TipoInsumo.AVIO;

    public enum UnidadMedida {
        METROS, UNIDADES, CONOS, KILOS, CENTIMETROS
    }
}
