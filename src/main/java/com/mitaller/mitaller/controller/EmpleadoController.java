package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Empleado;
import com.mitaller.mitaller.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

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
}