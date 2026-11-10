package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.EntregaSatelite;
import com.mitaller.mitaller.model.LiquidacionSatelite;
import com.mitaller.mitaller.service.TallerSateliteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/satelites")
public class TallerSateliteController {

    private final TallerSateliteService tallerSateliteService;

    public TallerSateliteController(TallerSateliteService tallerSateliteService) {
        this.tallerSateliteService = tallerSateliteService;
    }

    // Endpoint para registrar entregas asociadas a la Orden de Producción
    @PostMapping("/entregas/taller/{empresaId}/satelite/{sateliteId}/orden/{ordenId}")
    public ResponseEntity<EntregaSatelite> registrarEntrega(
            @PathVariable Long empresaId,
            @PathVariable Long sateliteId,
            @PathVariable Long ordenId,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal precioPorPrenda,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(tallerSateliteService.registrarEntrega(
                empresaId, sateliteId, ordenId, cantidad, precioPorPrenda, fecha));
    }

    // Endpoint para liquidar período de maquila
    @PostMapping("/liquidar/taller/{empresaId}/satelite/{sateliteId}")
    public ResponseEntity<LiquidacionSatelite> liquidarPeriodo(
            @PathVariable Long empresaId,
            @PathVariable Long sateliteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") BigDecimal retenciones,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(tallerSateliteService.liquidarPeriodo(
                empresaId, sateliteId, fechaInicio, fechaFin, retenciones, observaciones));
    }
}