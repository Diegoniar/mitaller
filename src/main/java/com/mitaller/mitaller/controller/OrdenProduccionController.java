package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.dto.OrdenProduccionCreacionDTO;
import com.mitaller.mitaller.model.OrdenProduccion;
import com.mitaller.mitaller.service.OrdenProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenProduccionController {

    private final OrdenProduccionService ordenProduccionService;

    public OrdenProduccionController(OrdenProduccionService ordenProduccionService) {
        this.ordenProduccionService = ordenProduccionService;
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<OrdenProduccion> crearOrden(
            @PathVariable Long empresaId,
            @RequestBody OrdenProduccionCreacionDTO dto) {
        return ResponseEntity.ok(ordenProduccionService.crearOrden(empresaId, dto));
    }

    @GetMapping("/{ordenId}/avance")
    public ResponseEntity verAvance(@PathVariable Long ordenId) {
        return ResponseEntity.ok(ordenProduccionService.consultarAvanceOrden(ordenId));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<OrdenProduccion>> listarPorEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(ordenProduccionService.listarPorEmpresa(empresaId));
    }
}