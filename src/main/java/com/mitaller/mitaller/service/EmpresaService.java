package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    // Al usar @RequiredArgsConstructor de Lombok, Spring inyecta esto automáticamente
    private final EmpresaRepository empresaRepository;

    public Empresa crearEmpresa(Empresa empresa) {
        // Regla de negocio: Validar que no exista otro taller con el mismo NIT
        if (empresaRepository.findByNit(empresa.getNit()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un taller registrado con este NIT");
        }
        return empresaRepository.save(empresa);
    }

    public List<Empresa> listarEmpresasActivas() {
        return empresaRepository.findAll().stream()
                .filter(Empresa::isActivo)
                .toList();
    }
}