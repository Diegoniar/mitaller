package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fases_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaseProduccion extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 50)
    private String nombre; // Ej: "Corte", "Fileteado", "Empaque"

    @Column(nullable = false)
    private Integer ordenFase; // Para saber qué paso va primero

    @Column(nullable = false)
    private boolean activo = true;
}