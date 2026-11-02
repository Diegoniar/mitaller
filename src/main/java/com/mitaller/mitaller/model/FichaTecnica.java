package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fichas_tecnicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichaTecnica extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 50)
    private String referencia; // Ej: "Jean Clásico Bota Recta REF-001"

    @Column(length = 255)
    private String descripcion;

    @Column(length = 100)
    private String tallas; // Ej: "S, M, L, XL"

    @Column(nullable = false)
    private boolean activo = true;

    // Relación uno a muchos: Una ficha tiene muchos insumos asociados
    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaInsumo> materiales = new ArrayList<>();
}