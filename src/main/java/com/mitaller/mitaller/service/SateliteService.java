package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SateliteService {

    private final EmpresaRepository empresaRepository;
    private final TallerSateliteRepository sateliteRepository;
    private final EntregaSateliteRepository entregaSateliteRepository;
    private final LiquidacionSateliteRepository liquidacionSateliteRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;

    @Transactional
    public TallerSatelite registrarSatelite(Long empresaId, TallerSatelite satelite) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller principal no existe"));
        satelite.setEmpresa(empresa);
        satelite.setActivo(true);
        return sateliteRepository.save(satelite);
    }

    @Transactional
    public EntregaSatelite registrarEntregaPrendas(
            Long empresaId, Long sateliteId, Long fichaId, Long ordenId,
            Integer cantidad, BigDecimal precioPorPrenda, LocalDate fecha) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller principal no existe"));
        TallerSatelite satelite = sateliteRepository.findById(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("El taller satélite no existe"));
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));

        OrdenProduccion orden = null;
        if (ordenId != null) {
            orden = ordenProduccionRepository.findById(ordenId).orElse(null);
        }

        BigDecimal total = precioPorPrenda.multiply(new BigDecimal(cantidad)).setScale(2, RoundingMode.HALF_UP);

        EntregaSatelite entrega = new EntregaSatelite();
        entrega.setEmpresa(empresa);
        entrega.setSatelite(satelite);
        entrega.setFichaTecnica(ficha);
        entrega.setOrdenProduccion(orden);
        entrega.setFechaEntrega(fecha);
        entrega.setCantidadPrendas(cantidad);
        entrega.setPrecioPorPrenda(precioPorPrenda);
        entrega.setTotalCobrado(total);
        entrega.setLiquidado(false);

        return entregaSateliteRepository.save(entrega);
    }

    @Transactional
    public LiquidacionSatelite liquidarPeriodoSatelite(
            Long empresaId, Long sateliteId, LocalDate inicio, LocalDate fin, BigDecimal descuentosRetenciones, String observaciones) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller principal no existe"));
        TallerSatelite satelite = sateliteRepository.findById(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("El taller satélite no existe"));

        // Obtener entregas no liquidadas en la semana/quincena
        List<EntregaSatelite> entregas = entregaSateliteRepository
                .findBySateliteIdAndFechaEntregaBetweenAndLiquidadoFalse(sateliteId, inicio, fin);

        if (entregas.isEmpty()) {
            throw new IllegalArgumentException("No hay entregas pendientes de liquidar para este satélite en el periodo indicado");
        }

        int totalPrendas = entregas.stream().mapToInt(EntregaSatelite::getCantidadPrendas).sum();
        BigDecimal subtotal = entregas.stream().map(EntregaSatelite::getTotalCobrado).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal retenciones = (descuentosRetenciones != null) ? descuentosRetenciones : BigDecimal.ZERO;
        BigDecimal neto = subtotal.subtract(retenciones).setScale(2, RoundingMode.HALF_UP);

        // Marcar entregas como liquidadas
        entregas.forEach(e -> e.setLiquidado(true));
        entregaSateliteRepository.saveAll(entregas);

        LiquidacionSatelite liquidacion = new LiquidacionSatelite();
        liquidacion.setEmpresa(empresa);
        liquidacion.setSatelite(satelite);
        liquidacion.setFechaInicio(inicio);
        liquidacion.setFechaFin(fin);
        liquidacion.setTotalPrendasConfeccionadas(totalPrendas);
        liquidacion.setSubtotalBruto(subtotal);
        liquidacion.setRetenciones(retenciones);
        liquidacion.setNetoAPagar(neto);
        liquidacion.setObservaciones(observaciones);

        return liquidacionSateliteRepository.save(liquidacion);
    }
}