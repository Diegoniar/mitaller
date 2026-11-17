package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/produccion-planta")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProduccionPlantaController {

    private final RegistroProduccionRepository registroProduccionRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final FaseProduccionRepository faseProduccionRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;

    // 1. Listar fases de confección (Corte, Filete, Plana, Ojal, Empaque...)
    @GetMapping("/fases")
    public ResponseEntity<List<FaseProduccion>> listarFases() {
        return ResponseEntity.ok(faseProduccionRepository.findAll());
    }

    // 2. Registrar lote o tarea realizada por un operario a destajo
    @PostMapping("/taller/{empresaId}")
    public ResponseEntity<RegistroProduccion> registrarTareaDestajo(
            @PathVariable Long empresaId,
            @RequestParam Long empleadoId,
            @RequestParam Long fichaTecnicaId,
            @RequestParam Long faseProduccionId,
            @RequestParam(required = false) Long ordenProduccionId,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal tarifaPorPrenda,
            @RequestParam(required = false) String fecha) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Taller no encontrado"));
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("Operario no encontrado"));
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha técnica no encontrada"));
        FaseProduccion fase = faseProduccionRepository.findById(faseProduccionId)
                .orElseThrow(() -> new IllegalArgumentException("Fase de producción no encontrada"));

        RegistroProduccion registro = new RegistroProduccion();
        registro.setEmpresa(empresa);
        registro.setEmpleado(empleado);
        registro.setFichaTecnica(ficha);
        registro.setFaseProduccion(fase);
        registro.setCantidadRealizada(cantidad);
        registro.setFechaTrabajo(fecha != null && !fecha.isBlank() ? LocalDate.parse(fecha) : LocalDate.now());
        registro.setTotalGanado(tarifaPorPrenda.multiply(BigDecimal.valueOf(cantidad)));

        if (ordenProduccionId != null) {
            ordenProduccionRepository.findById(ordenProduccionId)
                    .ifPresent(registro::setOrdenProduccion);
        }

        return ResponseEntity.ok(registroProduccionRepository.save(registro));
    }
}