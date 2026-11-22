package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
public class Cliente extends EntidadBase {

    @Column(nullable = false)
    private String nombre;

    private String nitOCedula;
    private String telefono;
    private String ciudad;
    private String direccion;
    private String correo; // Preparado para futura facturación electrónica

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Empresa empresa;
}