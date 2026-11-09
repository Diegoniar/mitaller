package com.mitaller.mitaller.dto;

import java.time.LocalDate;

public class OrdenProduccionCreacionDTO {
    private String numeroOrden; // Ej: OP-2026-002
    private Long fichaTecnicaId;
    private Integer cantidadProgramada;
    private Integer cantidadCortada;
    private LocalDate fechaInicio;
    private LocalDate fechaEntregaEstimada;

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public Long getFichaTecnicaId() { return fichaTecnicaId; }
    public void setFichaTecnicaId(Long fichaTecnicaId) { this.fichaTecnicaId = fichaTecnicaId; }
    public Integer getCantidadProgramada() { return cantidadProgramada; }
    public void setCantidadProgramada(Integer cantidadProgramada) { this.cantidadProgramada = cantidadProgramada; }
    public Integer getCantidadCortada() { return cantidadCortada; }
    public void setCantidadCortada(Integer cantidadCortada) { this.cantidadCortada = cantidadCortada; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }
}