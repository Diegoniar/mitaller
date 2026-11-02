package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.LiquidacionNomina;
import com.mitaller.mitaller.service.NominaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/nomina")
@RequiredArgsConstructor
public class NominaController {

    private final NominaService nominaService;

    // POST: Liquidar nómina para un operario en un rango de fechas
    @PostMapping("/liquidar/taller/{empresaId}/empleado/{empleadoId}")
    public ResponseEntity<LiquidacionNomina> liquidarSemana(
            @PathVariable Long empresaId,
            @PathVariable Long empleadoId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        LiquidacionNomina liquidacion = nominaService.liquidarSemanaEmpleado(empresaId, empleadoId, inicio, fin);
        return new ResponseEntity<>(liquidacion, HttpStatus.CREATED);
    }

    @GetMapping("/colilla/{liquidacionId}")
    public ResponseEntity<com.mitaller.mitaller.dto.ColillaPagoResponse> verColilla(@PathVariable Long liquidacionId) {
        return ResponseEntity.ok(nominaService.obtenerColillaDetallada(liquidacionId));
    }
}