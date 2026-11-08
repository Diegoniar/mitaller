package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.EntregaSatelite;
import com.mitaller.mitaller.model.LiquidacionSatelite;
import com.mitaller.mitaller.model.TallerSatelite;
import com.mitaller.mitaller.service.SateliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/satelites")
@RequiredArgsConstructor
public class SateliteController {

    private final SateliteService sateliteService;

    // 1. Crear Taller Satélite
    @PostMapping("/taller/{empresaId}")
    public ResponseEntity<TallerSatelite> registrarSatelite(
            @PathVariable Long empresaId,
            @RequestBody TallerSatelite satelite) {
        return new ResponseEntity<>(sateliteService.registrarSatelite(empresaId, satelite), HttpStatus.CREATED);
    }

    // 2. Registrar lote de prendas que el satélite entrega confeccionadas
    @PostMapping("/entregas/taller/{empresaId}/satelite/{sateliteId}/ficha/{fichaId}")
    public ResponseEntity<EntregaSatelite> registrarEntrega(
            @PathVariable Long empresaId,
            @PathVariable Long sateliteId,
            @PathVariable Long fichaId,
            @RequestParam(required = false) Long ordenId,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal precioPorPrenda,
            @RequestParam String fecha) {

        LocalDate fechaEntrega = LocalDate.parse(fecha);
        EntregaSatelite entrega = sateliteService.registrarEntregaPrendas(
                empresaId, sateliteId, fichaId, ordenId, cantidad, precioPorPrenda, fechaEntrega);
        return new ResponseEntity<>(entrega, HttpStatus.CREATED);
    }

    // 3. Liquidar el corte semanal o quincenal del satélite
    @PostMapping("/liquidar/taller/{empresaId}/satelite/{sateliteId}")
    public ResponseEntity<LiquidacionSatelite> liquidarPeriodo(
            @PathVariable Long empresaId,
            @PathVariable Long sateliteId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false, defaultValue = "0") BigDecimal retenciones,
            @RequestParam(required = false) String observaciones) {

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        LiquidacionSatelite liquidacion = sateliteService.liquidarPeriodoSatelite(
                empresaId, sateliteId, inicio, fin, retenciones, observaciones);
        return new ResponseEntity<>(liquidacion, HttpStatus.CREATED);
    }
}