package com.mitaller.mitaller.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gastos_operativos")
public class GastoOperativo extends EntidadBase {

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String categoria; // TRANSPORTE_DOMICILIOS, SERVICIOS_PUBLICOS, ARRIENDO, MANTENIMIENTO, INSUMOS_MENORES, OTROS

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    private String metodoPago; // EFECTIVO, TRANSFERENCIA, NEQUI
    private String comprobanteOProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Empresa empresa;
}