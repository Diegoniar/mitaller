package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "abonos_clientes")
public class AbonoCliente extends EntidadBase {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String metodoPago; // EFECTIVO, NEQUI, DAVIPLATA, BANCOLOMBIA, OTRO

    private String observaciones;

    @Column(precision = 14, scale = 2)
    private BigDecimal saldoAnteriorSnapshot;

    @Column(precision = 14, scale = 2)
    private BigDecimal saldoNuevoSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Empresa empresa;
}