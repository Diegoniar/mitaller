package com.mitaller.mitaller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proveedores")
public class Proveedor extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    private String nitOCedula;
    private String telefono;
    private String ciudad;
    private String categoria; // TELAS, AVIOS, LAVANDERIA, BORDADO_ESTAMPADO, SERVICIOS_VARIOS

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    private Boolean activo = true;
}