package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.Empleado;
import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.repository.EmpleadoRepository;
import com.mitaller.mitaller.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpresaRepository empresaRepository;

    public Empleado registrarEmpleado(Long empresaId, Empleado empleado) {
        // 1. Buscamos el taller en la base de datos
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller especificado no existe"));

        // 2. Le asignamos ese taller al empleado antes de guardarlo (Multi-tenant)
        empleado.setEmpresa(empresa);

        // 3. Guardamos en la base de datos
        return empleadoRepository.save(empleado);
    }

    public List<Empleado> listarEmpleadosDeTaller(Long empresaId) {
        return empleadoRepository.findByEmpresaIdAndActivoTrue(empresaId);
    }
}