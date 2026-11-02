package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.Empleado;
import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.LiquidacionNomina;
import com.mitaller.mitaller.model.RegistroProduccion;
import com.mitaller.mitaller.repository.EmpleadoRepository;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.LiquidacionNominaRepository;
import com.mitaller.mitaller.repository.RegistroProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NominaService {

    private final EmpresaRepository empresaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RegistroProduccionRepository registroProduccionRepository;
    private final LiquidacionNominaRepository liquidacionNominaRepository;

    // Factores de cálculo según la legislación colombiana
    private static final BigDecimal PORCENTAJE_SALUD_TRABAJADOR = new BigDecimal("0.04");
    private static final BigDecimal PORCENTAJE_PENSION_TRABAJADOR = new BigDecimal("0.04");

    // Provisiones obligatorias empleador: Prima (8.33%) + Cesantías (8.33%) + Int. Cesantías (1%) + Vacaciones (4.17%) = ~21.83%
    private static final BigDecimal FACTOR_PROVISIONES = new BigDecimal("0.2183");

    @Transactional
    public LiquidacionNomina liquidarSemanaEmpleado(Long empresaId, Long empleadoId, LocalDate inicio, LocalDate fin) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new IllegalArgumentException("El operario no existe"));

        // 1. Obtener los registros de trabajo del operario dentro del periodo
        List<RegistroProduccion> registros = registroProduccionRepository
                .findByEmpleadoIdAndFechaTrabajoBetween(empleadoId, inicio, fin);

        // 2. Sumar el destajo devengado
        BigDecimal totalDestajo = registros.stream()
                .map(RegistroProduccion::getTotalGanado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Calcular deducciones del trabajador (8% en total para contratos laborales)
        BigDecimal deduccionSalud = totalDestajo.multiply(PORCENTAJE_SALUD_TRABAJADOR);
        BigDecimal deduccionPension = totalDestajo.multiply(PORCENTAJE_PENSION_TRABAJADOR);
        BigDecimal deducciones = deduccionSalud.add(deduccionPension);

        // 4. Calcular provisiones empresariales (reserva de primas, cesantías y vacaciones)
        BigDecimal provisiones = totalDestajo.multiply(FACTOR_PROVISIONES);

        // 5. Determinar neto en efectivo/transferencia para el operario
        BigDecimal netoAPagar = totalDestajo.subtract(deducciones);

        LiquidacionNomina liquidacion = new LiquidacionNomina();
        liquidacion.setEmpresa(empresa);
        liquidacion.setEmpleado(empleado);
        liquidacion.setFechaInicio(inicio);
        liquidacion.setFechaFin(fin);
        liquidacion.setTotalDestajo(totalDestajo.setScale(2, RoundingMode.HALF_UP));
        liquidacion.setSalarioBaseProporcional(BigDecimal.ZERO);
        liquidacion.setAuxilioTransporte(BigDecimal.ZERO); // Ajustable si devenga menos de 2 SMMLV
        liquidacion.setDeducciones(deducciones.setScale(2, RoundingMode.HALF_UP));
        liquidacion.setProvisiones(provisiones.setScale(2, RoundingMode.HALF_UP));
        liquidacion.setNetoAPagar(netoAPagar.setScale(2, RoundingMode.HALF_UP));

        return liquidacionNominaRepository.save(liquidacion);
    }

    @Transactional(readOnly = true)
    public com.mitaller.mitaller.dto.ColillaPagoResponse obtenerColillaDetallada(Long liquidacionId) {
        LiquidacionNomina liquidacion = liquidacionNominaRepository.findById(liquidacionId)
                .orElseThrow(() -> new IllegalArgumentException("La liquidación de nómina no existe"));

        Empleado empleado = liquidacion.getEmpleado();
        Empresa empresa = liquidacion.getEmpresa();

        // 1. Obtener registros del operario en ese periodo exacto
        List<RegistroProduccion> registros = registroProduccionRepository
                .findByEmpleadoIdAndFechaTrabajoBetween(empleado.getId(), liquidacion.getFechaInicio(), liquidacion.getFechaFin());

        // 2. Mapear cada tarea realizada
        List<com.mitaller.mitaller.dto.ColillaPagoResponse.ItemProduccionDetalle> detalles = registros.stream()
                .map(r -> {
                    BigDecimal tarifa = r.getCantidadRealizada() > 0
                            ? r.getTotalGanado().divide(new BigDecimal(r.getCantidadRealizada()), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return com.mitaller.mitaller.dto.ColillaPagoResponse.ItemProduccionDetalle.builder()
                            .fecha(r.getFechaTrabajo())
                            .ordenProduccion(r.getOrdenProduccion() != null ? r.getOrdenProduccion().getNumeroOrden() : "S/O")
                            .referencia(r.getFichaTecnica().getReferencia())
                            .fase(r.getFaseProduccion().getNombre())
                            .cantidad(r.getCantidadRealizada())
                            .tarifaUnitaria(tarifa)
                            .subtotal(r.getTotalGanado())
                            .build();
                })
                .toList();

        // 3. Desglosar deducciones (4% salud, 4% pensión)
        BigDecimal deduccionIndividual = liquidacion.getTotalDestajo().multiply(PORCENTAJE_SALUD_TRABAJADOR).setScale(2, RoundingMode.HALF_UP);

        return com.mitaller.mitaller.dto.ColillaPagoResponse.builder()
                .empresaNombre(empresa.getRazonSocial())
                .empresaNit(empresa.getNit())
                .empleadoNombre(empleado.getNombreCompleto())
                .empleadoDocumento(empleado.getDocumentoIdentidad())
                .tipoContrato(empleado.getTipoVinculacion().name())
                .periodoInicio(liquidacion.getFechaInicio())
                .periodoFin(liquidacion.getFechaFin())
                .detallesProduccion(detalles)
                .totalDestajoBruto(liquidacion.getTotalDestajo())
                .descuentoSalud(deduccionIndividual)
                .descuentoPension(deduccionIndividual)
                .totalDeducciones(liquidacion.getDeducciones())
                .netoAPagar(liquidacion.getNetoAPagar())
                .provisionesEmpresa(liquidacion.getProvisiones())
                .build();
    }
}