package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.service.InsumoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
@CrossOrigin(origins = "*")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
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
}