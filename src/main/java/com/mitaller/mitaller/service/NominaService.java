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

        // Calcular días calendario del periodo (inclusive)
        long diasPeriodo = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (diasPeriodo <= 0) diasPeriodo = 1;

        // 1. Obtener los registros de destajo/comisiones del operario dentro del periodo
        List<RegistroProduccion> registros = registroProduccionRepository
                .findByEmpleadoIdAndFechaTrabajoBetween(empleadoId, inicio, fin);

        BigDecimal totalDestajo = registros.stream()
                .map(RegistroProduccion::getTotalGanado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Calcular salario base proporcional al periodo (Art. 133 y 147 CST)
        BigDecimal salarioBase = empleado.getSalarioBase() != null ? empleado.getSalarioBase() : BigDecimal.ZERO;
        BigDecimal salarioBaseProporcional = BigDecimal.ZERO;

        if (salarioBase.compareTo(BigDecimal.ZERO) > 0) {
            salarioBaseProporcional = salarioBase
                    .divide(new BigDecimal("30"), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(diasPeriodo));
        }

        // 3. Descanso dominical remunerado sobre salario variable (Art. 172 y 176 CST)
        // Si es contrato laboral con destajo y cubrió semana completa (6 o más días), se reconoce 1/6 del destajo
        BigDecimal dominicalVariable = BigDecimal.ZERO;
        if (empleado.getTipoVinculacion() != Empleado.TipoVinculacion.PRESTACION_SERVICIOS
                && totalDestajo.compareTo(BigDecimal.ZERO) > 0
                && diasPeriodo >= 6) {
            dominicalVariable = totalDestajo.divide(new BigDecimal("6"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalDestajoConDominical = totalDestajo.add(dominicalVariable);

        // 4. Total Devengado Salarial (Art. 127 CST: Base + Destajo/Comisiones + Dominical)
        BigDecimal totalDevengadoSalarial = salarioBaseProporcional.add(totalDestajoConDominical);

        // 5. Auxilio de transporte proporcional (si aplica: contrato laboral y devenga <= 2 SMMLV)
        BigDecimal auxilioTransporte = BigDecimal.ZERO;
        BigDecimal smmlvReferencia = new BigDecimal("1423500"); // Ajustable según año vigente
        BigDecimal auxilioMensual = new BigDecimal("200000");

        if (empleado.getTipoVinculacion() != Empleado.TipoVinculacion.PRESTACION_SERVICIOS
                && totalDevengadoSalarial.compareTo(BigDecimal.ZERO) > 0
                && salarioBase.compareTo(smmlvReferencia.multiply(new BigDecimal("2"))) <= 0) {
            auxilioTransporte = auxilioMensual
                    .divide(new BigDecimal("30"), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(diasPeriodo));
        }

        // 6. Deducciones de Ley (4% Salud + 4% Pensión sobre el devengado salarial, sin incluir auxilio de transporte)
        BigDecimal deducciones = BigDecimal.ZERO;
        BigDecimal provisiones = BigDecimal.ZERO;

        if (empleado.getTipoVinculacion() != Empleado.TipoVinculacion.PRESTACION_SERVICIOS) {
            BigDecimal deduccionSalud = totalDevengadoSalarial.multiply(PORCENTAJE_SALUD_TRABAJADOR);
            BigDecimal deduccionPension = totalDevengadoSalarial.multiply(PORCENTAJE_PENSION_TRABAJADOR);
            deducciones = deduccionSalud.add(deduccionPension);

            // 7. Provisiones Prestacionales del Empleador (Prima Art. 306, Cesantías Art. 249, Int. Cesantías, Vacaciones Art. 186)
            // Prima y Cesantías incluyen auxilio de transporte; Vacaciones solo sobre salario base/variable
            BigDecimal basePrestaciones = totalDevengadoSalarial.add(auxilioTransporte);
            BigDecimal provPrimaYCesantias = basePrestaciones.multiply(new BigDecimal("0.1766")); // 8.33% + 8.33% + 1%
            BigDecimal provVacaciones = totalDevengadoSalarial.multiply(new BigDecimal("0.0417")); // 4.17%
            provisiones = provPrimaYCesantias.add(provVacaciones);
        }

        // 8. Neto real a transferir al trabajador
        BigDecimal netoAPagar = totalDevengadoSalarial.add(auxilioTransporte).subtract(deducciones);

        LiquidacionNomina liquidacion = new LiquidacionNomina();
        liquidacion.setEmpresa(empresa);
        liquidacion.setEmpleado(empleado);
        liquidacion.setFechaInicio(inicio);
        liquidacion.setFechaFin(fin);
        liquidacion.setTotalDestajo(totalDestajoConDominical.setScale(2, RoundingMode.HALF_UP));
        liquidacion.setSalarioBaseProporcional(salarioBaseProporcional.setScale(2, RoundingMode.HALF_UP));
        liquidacion.setAuxilioTransporte(auxilioTransporte.setScale(2, RoundingMode.HALF_UP));
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