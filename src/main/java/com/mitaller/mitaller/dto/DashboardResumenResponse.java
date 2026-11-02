package com.mitaller.mitaller.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResumenResponse {
    private Long empresaId;
    private String nombreEmpresa;

    // Métricas operativas
    private long ordenesActivas;
    private int totalPrendasProducidasMes;

    // Métricas financieras
    private BigDecimal totalNominaPagadaMes;
    private BigDecimal provisionesAcumuladasMes;

    // Rendimiento del equipo
    private List<OperarioRendimientoDTO> topOperarios;

    @Getter
    @Builder
    public static class OperarioRendimientoDTO {
        private String nombreOperario;
        private int prendasRealizadas;
        private BigDecimal totalGanado;
    }
}