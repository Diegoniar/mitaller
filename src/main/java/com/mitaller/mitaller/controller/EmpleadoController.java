package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Empleado;
import com.mitaller.mitaller.repository.EmpleadoRepository;
import com.mitaller.mitaller.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final EmpleadoRepository empleadoRepository;
    // POST: Para registrar un operario en un taller específico
    @PostMapping("/taller/{empresaId}")
    public ResponseEntity<Empleado> registrarEmpleado(
            @PathVariable Long empresaId,
            @RequestBody Empleado empleado) {

        Empleado nuevoEmpleado = empleadoService.registrarEmpleado(empresaId, empleado);
        return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
    }

    // GET: Para ver la lista de empleados de un taller
    @GetMapping("/taller/{empresaId}")
    public ResponseEntity<List<Empleado>> listarEmpleados(@PathVariable Long empresaId) {
        return ResponseEntity.ok(empleadoService.listarEmpleadosDeTaller(empresaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarEmpleado(@PathVariable Long id) {
        return empleadoRepository.findById(id)
                .map(emp -> {
                    emp.setActivo(false);
                    empleadoRepository.save(emp);
                    return ResponseEntity.ok(Map.of("mensaje", "Empleado retirado de nómina activa"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}