package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.GastoOperativo;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.GastoOperativoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*")
public class GastoOperativoController {

    private final GastoOperativoRepository gastoRepository;
    private final EmpresaRepository empresaRepository;

    public GastoOperativoController(GastoOperativoRepository gastoRepository, EmpresaRepository empresaRepository) {
        this.gastoRepository = gastoRepository;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping("/empresa/{empresaId}")
    public List<GastoOperativo> listarGastos(@PathVariable Long empresaId) {
        return gastoRepository.findByEmpresaIdOrderByFechaDescIdDesc(empresaId);
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<?> registrarGasto(@PathVariable Long empresaId, @RequestBody GastoOperativo gasto) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        gasto.setEmpresa(empresa);
        if (gasto.getFecha() == null) {
            gasto.setFecha(LocalDate.now());
        }
        return ResponseEntity.ok(gastoRepository.save(gasto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarGasto(@PathVariable Long id) {
        if (!gastoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        gastoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Gasto eliminado"));
    }
}