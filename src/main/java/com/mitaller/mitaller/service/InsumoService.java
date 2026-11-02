package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final EmpresaRepository empresaRepository;

    public Insumo registrarInsumo(Long empresaId, Insumo insumo) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));

        insumo.setEmpresa(empresa);
        return insumoRepository.save(insumo);
    }

    public List<Insumo> listarInsumosPorTaller(Long empresaId) {
        return insumoRepository.findByEmpresaIdAndActivoTrue(empresaId);
    }
}