package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.FichaTecnicaRepository;
import com.mitaller.mitaller.repository.OrdenProduccionRepository;
import com.mitaller.mitaller.repository.RegistroProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrdenProduccionService {

    private final OrdenProduccionRepository ordenProduccionRepository;
    private final EmpresaRepository empresaRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final RegistroProduccionRepository registroProduccionRepository;

    public OrdenProduccion crearOrden(Long empresaId, Long fichaId, OrdenProduccion orden) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));

        orden.setEmpresa(empresa);
        orden.setFichaTecnica(ficha);
        orden.setEstado(EstadoOrden.EN_PROCESO);
        return ordenProduccionRepository.save(orden);
    }

    // Permite conocer el avance real frente a lo programado
    public Map<String, Object> consultarAvanceOrden(Long ordenId) {
        OrdenProduccion orden = ordenProduccionRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("La orden de producción no existe"));

        List<RegistroProduccion> registros = registroProduccionRepository.findAll().stream()
                .filter(r -> r.getOrdenProduccion() != null && r.getOrdenProduccion().getId().equals(ordenId))
                .toList();

        int unidadesProcesadas = registros.stream()
                .mapToInt(RegistroProduccion::getCantidadRealizada)
                .sum();

        double porcentajeAvance = orden.getCantidadProgramada() > 0
                ? ((double) unidadesProcesadas / orden.getCantidadProgramada()) * 100
                : 0.0;

        return Map.of(
                "numeroOrden", orden.getNumeroOrden(),
                "referenciaPrenda", orden.getFichaTecnica().getReferencia(),
                "cantidadProgramada", orden.getCantidadProgramada(),
                "totalConfeccionado", unidadesProcesadas,
                "porcentajeAvance", Math.min(porcentajeAvance, 100.0),
                "estado", orden.getEstado().name()
        );
    }
}