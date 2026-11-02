package com.mitaller.mitaller.service;

import com.mitaller.mitaller.dto.DashboardResumenResponse;
import com.mitaller.mitaller.dto.DashboardResumenResponse.OperarioRendimientoDTO;
import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.EstadoOrden;
import com.mitaller.mitaller.model.LiquidacionNomina;
import com.mitaller.mitaller.model.RegistroProduccion;
import com.mitaller.mitaller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmpresaRepository empresaRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;
    private final RegistroProduccionRepository registroProduccionRepository;
    private final LiquidacionNominaRepository liquidacionNominaRepository;

    @Transactional(readOnly = true)
    public DashboardResumenResponse obtenerResumenMensual(Long empresaId, int anio, int mes) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));

        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // 1. Órdenes en proceso
        long ordenesActivas = ordenProduccionRepository.findByEmpresaIdAndEstado(empresaId, EstadoOrden.EN_PROCESO).size();

        // 2. Registros de producción del mes
        List<RegistroProduccion> registrosMes = registroProduccionRepository.findAll().stream()
                .filter(r -> r.getEmpresa().getId().equals(empresaId)
                        && !r.getFechaTrabajo().isBefore(inicioMes)
                        && !r.getFechaTrabajo().isAfter(finMes))
                .toList();

        int totalPrendasProducidas = registrosMes.stream()
                .mapToInt(RegistroProduccion::getCantidadRealizada)
                .sum();

        // 3. Liquidaciones de nómina del mes
        List<LiquidacionNomina> liquidacionesMes = liquidacionNominaRepository.findAll().stream()
                .filter(l -> l.getEmpresa().getId().equals(empresaId)
                        && !l.getFechaFin().isBefore(inicioMes)
                        && !l.getFechaFin().isAfter(finMes))
                .toList();

        BigDecimal totalNominaPagada = liquidacionesMes.stream()
                .map(LiquidacionNomina::getNetoAPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal provisionesAcumuladas = liquidacionesMes.stream()
                .map(LiquidacionNomina::getProvisiones)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Ranking de operarios
        Map<String, List<RegistroProduccion>> porOperario = registrosMes.stream()
                .collect(Collectors.groupingBy(r -> r.getEmpleado().getNombreCompleto()));

        List<OperarioRendimientoDTO> topOperarios = porOperario.entrySet().stream()
                .map(entry -> OperarioRendimientoDTO.builder()
                        .nombreOperario(entry.getKey())
                        .prendasRealizadas(entry.getValue().stream().mapToInt(RegistroProduccion::getCantidadRealizada).sum())
                        .totalGanado(entry.getValue().stream().map(RegistroProduccion::getTotalGanado).reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .sorted((a, b) -> b.getTotalGanado().compareTo(a.getTotalGanado()))
                .limit(5)
                .toList();

        return DashboardResumenResponse.builder()
                .empresaId(empresa.getId())
                .nombreEmpresa(empresa.getRazonSocial())
                .ordenesActivas(ordenesActivas)
                .totalPrendasProducidasMes(totalPrendasProducidas)
                .totalNominaPagadaMes(totalNominaPagada)
                .provisionesAcumuladasMes(provisionesAcumuladas)
                .topOperarios(topOperarios)
                .build();
    }
}