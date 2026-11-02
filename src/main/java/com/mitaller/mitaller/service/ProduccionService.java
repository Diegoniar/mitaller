package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduccionService {

    private final EmpresaRepository empresaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final FaseProduccionRepository faseProduccionRepository;
    private final TarifaDestajoRepository tarifaDestajoRepository;
    private final RegistroProduccionRepository registroProduccionRepository;
    private final OrdenProduccionRepository ordenProduccionRepository;

    @Transactional
    public TarifaDestajo configurarTarifa(TarifaDestajo tarifa) {
        return tarifaDestajoRepository.save(tarifa);
    }

    @Transactional
    public RegistroProduccion registrarTrabajoDiario(
            Long empresaId,
            Long empleadoId,
            Long fichaId,
            Long faseId,
            Long ordenId,
            Integer cantidad,
            LocalDate fecha) {

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("El operario no existe"));
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));
        FaseProduccion fase = faseProduccionRepository.findById(faseId)
                .orElseThrow(() -> new IllegalArgumentException("La fase de producción no existe"));
        OrdenProduccion orden = ordenProduccionRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("La orden de producción no existe"));

        // 1. Control de sobreproducción: verificar acumulado de la fase en esta orden
        List<RegistroProduccion> registrosPrevios = registroProduccionRepository.findAll().stream()
                .filter(r -> r.getOrdenProduccion() != null
                        && r.getOrdenProduccion().getId().equals(ordenId)
                        && r.getFaseProduccion().getId().equals(faseId))
                .toList();

        int unidadesYaRegistradas = registrosPrevios.stream()
                .mapToInt(RegistroProduccion::getCantidadRealizada)
                .sum();

        int limitePermitido = orden.getCantidadProgramada();
        if (unidadesYaRegistradas + cantidad > limitePermitido) {
            int cupoDisponible = limitePermitido - unidadesYaRegistradas;
            throw new IllegalArgumentException(String.format(
                    "Límite excedido para la orden %s en la fase '%s'. Programadas: %d, ya confeccionadas: %d. Cupo disponible restante: %d",
                    orden.getNumeroOrden(), fase.getNombre(), limitePermitido, unidadesYaRegistradas, Math.max(cupoDisponible, 0)
            ));
        }

        // 2. Buscar tarifa y calcular pago
        TarifaDestajo tarifa = tarifaDestajoRepository.findByFichaTecnicaIdAndFaseProduccionId(fichaId, faseId)
                .orElseThrow(() -> new IllegalArgumentException("No hay una tarifa configurada para esta fase y prenda"));

        BigDecimal totalGanado = tarifa.getValorPorUnidad().multiply(new BigDecimal(cantidad));

        // 3. Crear y guardar el registro vinculado a la orden
        RegistroProduccion registro = new RegistroProduccion();
        registro.setEmpresa(empresa);
        registro.setEmpleado(empleado);
        registro.setFichaTecnica(ficha);
        registro.setFaseProduccion(fase);
        registro.setOrdenProduccion(orden);
        registro.setCantidadRealizada(cantidad);
        registro.setFechaTrabajo(fecha);
        registro.setTotalGanado(totalGanado);

        return registroProduccionRepository.save(registro);
    }
}