package com.mitaller.mitaller.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ColillaPagoResponse {
    private String empresaNombre;
    private String empresaNit;
    private String empleadoNombre;
    private String empleadoDocumento;
    private String tipoContrato;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;

    // Desglose de actividades
    private List<ItemProduccionDetalle> detallesProduccion;

    // Totales financieros
    private BigDecimal totalDestajoBruto;
    private BigDecimal descuentoSalud;
    private BigDecimal descuentoPension;
    private BigDecimal totalDeducciones;
    private BigDecimal netoAPagar;

    // Informativo para la empresa (reserva patronal)
    private BigDecimal provisionesEmpresa;

    @Getter
    @Builder
    public static class ItemProduccionDetalle {
        private LocalDate fecha;
        private String ordenProduccion;
        private String referencia;
        private String fase;
        private Integer cantidad;
        private BigDecimal tarifaUnitaria;
        private BigDecimal subtotal;
    }
}