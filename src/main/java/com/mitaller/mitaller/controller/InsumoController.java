package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.repository.InsumoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
@CrossOrigin(origins = "*")
public class InsumoController {

    private final InsumoRepository insumoRepository;

    public InsumoController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Insumo>> listarInsumos(@PathVariable Long empresaId) {
        return ResponseEntity.ok(insumoRepository.findByEmpresaIdAndActivoTrue(empresaId));
    }


}