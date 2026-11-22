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
@Table(name = "compras_proveedores")
public class CompraProveedor extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    private String numeroFactura;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String tipoCompra; // INSUMOS_TELAS, AVIOS, SERVICIO_LAVANDERIA, SERVICIO_BORDADO, OTRO_SERVICIO

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCompra = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal abonoInicial = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoPendienteCompra = BigDecimal.ZERO;

    private String metodoPagoAbono;
}