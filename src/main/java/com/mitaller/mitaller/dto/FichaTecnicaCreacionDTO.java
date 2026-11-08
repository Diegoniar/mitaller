package com.mitaller.mitaller.dto;

import java.math.BigDecimal;
import java.util.List;

public class FichaTecnicaCreacionDTO {
    private String referencia;
    private String descripcion;
    private String tallas; // Ej: "28,30,32,34"
    private List<InsumoConsumoDTO> insumos;

    public static class InsumoConsumoDTO {
        private Long insumoId;
        private BigDecimal cantidadNecesaria;
        private BigDecimal porcentajeMerma;

        public Long getInsumoId() { return insumoId; }
        public void setInsumoId(Long insumoId) { this.insumoId = insumoId; }
        public BigDecimal getCantidadNecesaria() { return cantidadNecesaria; }
        public void setCantidadNecesaria(BigDecimal cantidadNecesaria) { this.cantidadNecesaria = cantidadNecesaria; }
        public BigDecimal getPorcentajeMerma() { return porcentajeMerma; }
        public void setPorcentajeMerma(BigDecimal porcentajeMerma) { this.porcentajeMerma = porcentajeMerma; }
    }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTallas() { return tallas; }
    public void setTallas(String tallas) { this.tallas = tallas; }
    public List<InsumoConsumoDTO> getInsumos() { return insumos; }
    public void setInsumos(List<InsumoConsumoDTO> insumos) { this.insumos = insumos; }
}