package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.EntregaSatelite;
import com.mitaller.mitaller.model.LiquidacionSatelite;
import com.mitaller.mitaller.model.OrdenProduccion;
import com.mitaller.mitaller.model.TallerSatelite;
import com.mitaller.mitaller.repository.EntregaSateliteRepository;
import com.mitaller.mitaller.repository.LiquidacionSateliteRepository;
import com.mitaller.mitaller.repository.OrdenProduccionRepository;
import com.mitaller.mitaller.repository.TallerSateliteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TallerSateliteService {

    private final TallerSateliteRepository tallerSateliteRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;
    private final EntregaSateliteRepository entregaSateliteRepository;
    private final LiquidacionSateliteRepository liquidacionSateliteRepository;

    public TallerSateliteService(
            TallerSateliteRepository tallerSateliteRepository,
            OrdenProduccionRepository ordenProduccionRepository,
            EntregaSateliteRepository entregaSateliteRepository,
            LiquidacionSateliteRepository liquidacionSateliteRepository) {
        this.tallerSateliteRepository = tallerSateliteRepository;
        this.ordenProduccionRepository = ordenProduccionRepository;
        this.entregaSateliteRepository = entregaSateliteRepository;
        this.liquidacionSateliteRepository = liquidacionSateliteRepository;
    }

    @Transactional
    public EntregaSatelite registrarEntrega(
            Long empresaId,
            Long sateliteId,
            Long ordenProduccionId,
            Integer cantidad,
            BigDecimal precioPorPrenda,
            LocalDate fecha) {

        TallerSatelite satelite = tallerSateliteRepository.findById(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("Taller satélite no encontrado"));

        OrdenProduccion orden = ordenProduccionRepository.findById(ordenProduccionId)
                .orElseThrow(() -> new IllegalArgumentException("Orden de producción no encontrada"));

        BigDecimal total = precioPorPrenda.multiply(BigDecimal.valueOf(cantidad));

        EntregaSatelite entrega = new EntregaSatelite();
        entrega.setEmpresa(orden.getEmpresa());
        entrega.setSatelite(satelite);
        entrega.setOrdenProduccion(orden);
        entrega.setFichaTecnica(orden.getFichaTecnica());
        entrega.setCantidadPrendas(cantidad);
        entrega.setPrecioPorPrenda(precioPorPrenda);

        // Asignación de ambos campos para compatibilidad con la base de datos
        entrega.setTotalPagar(total);
        entrega.setTotalCobrado(total);

        entrega.setFechaEntrega(fecha != null ? fecha : LocalDate.now());
        entrega.setLiquidada(false);
        entrega.setLiquidado(false);

        return entregaSateliteRepository.save(entrega);
    }

    @Transactional
    public LiquidacionSatelite liquidarPeriodo(
            Long empresaId,
            Long sateliteId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            BigDecimal retenciones,
            String observaciones) {

        TallerSatelite satelite = tallerSateliteRepository.findById(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("Taller satélite no encontrado"));

        List<EntregaSatelite> entregas = entregaSateliteRepository
                .findByEmpresaIdAndSateliteIdAndLiquidadaFalseAndFechaEntregaBetween(
                        empresaId, sateliteId, fechaInicio, fechaFin);

        if (entregas.isEmpty()) {
            throw new IllegalArgumentException("No hay entregas pendientes de liquidar para este satélite en el periodo indicado");
        }

        int totalPrendas = entregas.stream()
                .mapToInt(EntregaSatelite::getCantidadPrendas)
                .sum();

        BigDecimal subtotal = entregas.stream()
                .map(EntregaSatelite::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deducciones = retenciones != null ? retenciones : BigDecimal.ZERO;
        BigDecimal neto = subtotal.subtract(deducciones);

        LiquidacionSatelite liquidacion = new LiquidacionSatelite();
        liquidacion.setEmpresa(satelite.getEmpresa());
        liquidacion.setSatelite(satelite);
        liquidacion.setFechaInicio(fechaInicio);
        liquidacion.setFechaFin(fechaFin);
        liquidacion.setTotalPrendasConfeccionadas(totalPrendas);
        liquidacion.setSubtotalBruto(subtotal);
        liquidacion.setRetenciones(deducciones);
        liquidacion.setNetoAPagar(neto);
        liquidacion.setObservaciones(observaciones);

        LiquidacionSatelite guardada = liquidacionSateliteRepository.save(liquidacion);

        entregas.forEach(e -> {
            e.setLiquidada(true);
            e.setLiquidacion(guardada);
            entregaSateliteRepository.save(e);
        });

        return guardada;
    }
}