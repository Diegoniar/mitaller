package com.mitaller.mitaller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "abonos_proveedores")
public class AbonoProveedor extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, NEQUI, DAVIPLATA

    private String observaciones;

    @Column(precision = 14, scale = 2)
    private BigDecimal saldoAnteriorSnapshot;

    @Column(precision = 14, scale = 2)
    private BigDecimal saldoNuevoSnapshot;
}