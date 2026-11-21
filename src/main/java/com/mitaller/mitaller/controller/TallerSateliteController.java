package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.EntregaSatelite;
import com.mitaller.mitaller.model.LiquidacionSatelite;
import com.mitaller.mitaller.model.TallerSatelite;
import com.mitaller.mitaller.repository.TallerSateliteRepository;
import com.mitaller.mitaller.service.TallerSateliteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/satelites")
public class TallerSateliteController {

    private final TallerSateliteService tallerSateliteService;
    private final TallerSateliteRepository tallerSateliteRepository;

    public TallerSateliteController(TallerSateliteService tallerSateliteService, TallerSateliteRepository tallerSateliteRepository) {
        this.tallerSateliteService = tallerSateliteService;
        this.tallerSateliteRepository = tallerSateliteRepository;
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

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<TallerSatelite>> listarSatelites(@PathVariable Long empresaId) {
        return ResponseEntity.ok(tallerSateliteService.listarPorEmpresa(empresaId));
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<TallerSatelite> crearSatelite(
            @PathVariable Long empresaId,
            @RequestBody TallerSatelite satelite) {
        return ResponseEntity.ok(tallerSateliteService.crearSatelite(empresaId, satelite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarSatelite(@PathVariable Long id) {
        return tallerSateliteRepository.findById(id)
                .map(sat -> {
                    sat.setActivo(false);
                    tallerSateliteRepository.save(sat);
                    return ResponseEntity.ok(Map.of("mensaje", "Taller satélite desactivado"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}