package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.dto.DashboardResumenResponse;
import com.mitaller.mitaller.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/taller/{empresaId}")
    public ResponseEntity<DashboardResumenResponse> verDashboard(
            @PathVariable Long empresaId,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes) {

        LocalDate hoy = LocalDate.now();
        int anioConsulta = (anio != null) ? anio : hoy.getYear();
        int mesConsulta = (mes != null) ? mes : hoy.getMonthValue();

        return ResponseEntity.ok(dashboardService.obtenerResumenMensual(empresaId, anioConsulta, mesConsulta));
    }
}