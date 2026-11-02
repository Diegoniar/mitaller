package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.service.InsumoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
@RequiredArgsConstructor
public class InsumoController {

    private final InsumoService insumoService;

    @PostMapping("/taller/{empresaId}")
    public ResponseEntity<Insumo> registrarInsumo(@PathVariable Long empresaId, @RequestBody Insumo insumo) {
        return new ResponseEntity<>(insumoService.registrarInsumo(empresaId, insumo), HttpStatus.CREATED);
    }

    @GetMapping("/taller/{empresaId}")
    public ResponseEntity<List<Insumo>> listarInsumos(@PathVariable Long empresaId) {
        return ResponseEntity.ok(insumoService.listarInsumosPorTaller(empresaId));
    }
}