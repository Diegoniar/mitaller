package com.mitaller.mitaller.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ficha_insumos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichaInsumo extends EntidadBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    @JsonBackReference
    private FichaTecnica fichaTecnica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal cantidadNecesaria;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMerma;
}