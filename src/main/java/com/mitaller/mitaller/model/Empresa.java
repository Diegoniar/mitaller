package com.mitaller.mitaller.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa extends EntidadBase{
    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    @Column(nullable = false, length = 100)
    private String razonSocial;

    @Column(length = 50)
    private String ciudad; // Importante para tu expansión: Cúcuta, Medellín, etc.

    @Column(nullable = false)
    private boolean activo = true;
}
