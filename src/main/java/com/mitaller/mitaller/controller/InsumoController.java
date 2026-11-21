package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.repository.InsumoRepository;
import com.mitaller.mitaller.service.InsumoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insumos")
@CrossOrigin(origins = "*")
public class InsumoController {

    private final InsumoService insumoService;
    private final InsumoRepository insumoRepository;

    public InsumoController(InsumoService insumoService, InsumoRepository insumoRepository) {
        this.insumoService = insumoService;
        this.insumoRepository = insumoRepository;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Insumo>> listarInsumos(@PathVariable Long empresaId) {
        return ResponseEntity.ok(insumoService.listarInsumosPorTaller(empresaId));
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<Insumo> crearInsumo(
            @PathVariable Long empresaId,
            @RequestBody Insumo insumo) {
        return ResponseEntity.ok(insumoService.registrarInsumo(empresaId, insumo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarInsumo(@PathVariable Long id) {
        return insumoRepository.findById(id)
                .map(insumo -> {
                    insumo.setActivo(false);
                    insumoRepository.save(insumo);
                    return ResponseEntity.ok(Map.of("mensaje", "Insumo desactivado correctamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}