package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.OrdenProduccion;
import com.mitaller.mitaller.service.OrdenProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenProduccionController {

    private final OrdenProduccionService ordenProduccionService;

    @PostMapping("/taller/{empresaId}/ficha/{fichaId}")
    public ResponseEntity<OrdenProduccion> crearOrden(
            @PathVariable Long empresaId,
            @PathVariable Long fichaId,
            @RequestBody OrdenProduccion orden) {

        return new ResponseEntity<>(ordenProduccionService.crearOrden(empresaId, fichaId, orden), HttpStatus.CREATED);
    }

    @GetMapping("/{ordenId}/avance")
    public ResponseEntity verAvance(@PathVariable Long ordenId) {
        return ResponseEntity.ok(ordenProduccionService.consultarAvanceOrden(ordenId));
    }
}