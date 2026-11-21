package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.dto.FichaTecnicaCreacionDTO;
import com.mitaller.mitaller.model.FichaInsumo;
import com.mitaller.mitaller.model.FichaTecnica;
import com.mitaller.mitaller.repository.FichaTecnicaRepository;
import com.mitaller.mitaller.service.FichaTecnicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fichas")
@RequiredArgsConstructor
public class FichaTecnicaController {

    private final FichaTecnicaService fichaTecnicaService;
    private final FichaTecnicaRepository fichaTecnicaRepository;

    // 1. Crear la ficha base (Ej: Pantalón)
    @PostMapping("/taller/{empresaId}")
    public ResponseEntity<FichaTecnica> crearFicha(@PathVariable Long empresaId, @RequestBody FichaTecnica ficha) {
        return new ResponseEntity<>(fichaTecnicaService.crearFicha(empresaId, ficha), HttpStatus.CREATED);
    }

    // 2. Agregar un insumo a la ficha (Ej: 1.5 metros de tela al Pantalón)
    @PostMapping("/{fichaId}/insumos/{insumoId}")
    public ResponseEntity<FichaInsumo> agregarInsumo(
            @PathVariable Long fichaId,
            @PathVariable Long insumoId,
            @RequestBody FichaInsumo detalle) {

        return new ResponseEntity<>(fichaTecnicaService.agregarInsumoAFicha(fichaId, insumoId, detalle), HttpStatus.CREATED);
    }

    // 3. Calcular el costo total en tiempo real
    @GetMapping("/{fichaId}/costo")
    public ResponseEntity calcularCosto(@PathVariable Long fichaId) {
        BigDecimal costo = fichaTecnicaService.calcularCostoMateriales(fichaId);
        return ResponseEntity.ok(Map.of("costoTotal", costo));
    }

    @GetMapping("/{fichaId}/resumen-costos")
    public ResponseEntity obtenerResumenCostos(
            @PathVariable Long fichaId,
            @RequestParam(required = false, defaultValue = "0") BigDecimal precioVenta) {

        return ResponseEntity.ok(fichaTecnicaService.calcularEstructuraCostosDetallada(fichaId, precioVenta));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<FichaTecnica>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(fichaTecnicaService.listarPorEmpresa(empresaId));
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<FichaTecnica> crearFicha(
            @PathVariable Long empresaId,
            @RequestBody FichaTecnicaCreacionDTO dto) {
        return ResponseEntity.ok(fichaTecnicaService.crearFichaCompleta(empresaId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFicha(@PathVariable Long id) {
        if (!fichaTecnicaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            fichaTecnicaRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensaje", "Ficha técnica eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "No se puede eliminar esta prenda porque ya tiene órdenes o producción asociada."));
        }
    }
}
