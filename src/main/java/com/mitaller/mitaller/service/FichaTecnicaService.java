package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final EmpresaRepository empresaRepository;
    private final InsumoRepository insumoRepository;
    private final FichaInsumoRepository fichaInsumoRepository;
    private final TarifaDestajoRepository tarifaDestajoRepository;

    public FichaTecnica crearFicha(Long empresaId, FichaTecnica ficha) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("El taller no existe"));

        ficha.setEmpresa(empresa);
        return fichaTecnicaRepository.save(ficha);
    }

    // Método clave para agregar tela, botones o hilos a una prenda
    @Transactional
    public FichaInsumo agregarInsumoAFicha(Long fichaId, Long insumoId, FichaInsumo detalle) {
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));

        Insumo insumo = insumoRepository.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("El insumo no existe"));

        detalle.setFichaTecnica(ficha);
        detalle.setInsumo(insumo);

        return fichaInsumoRepository.save(detalle);
    }

    // El cálculo de costos en tiempo real
    public BigDecimal calcularCostoMateriales(Long fichaId) {
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));

        BigDecimal costoTotal = BigDecimal.ZERO;

        for (FichaInsumo detalle : ficha.getMateriales()) {
            BigDecimal costoUnitario = detalle.getInsumo().getCostoUnitario();
            BigDecimal cantidad = detalle.getCantidadNecesaria();

            // Si hay un 5% de merma (0.05), multiplicamos por 1.05
            BigDecimal factorMerma = BigDecimal.ONE.add(
                    detalle.getPorcentajeMerma() != null ? detalle.getPorcentajeMerma() : BigDecimal.ZERO
            );

            // Costo = (Costo Unitario * Cantidad) * (1 + Merma)
            BigDecimal costoParcial = costoUnitario.multiply(cantidad).multiply(factorMerma);
            costoTotal = costoTotal.add(costoParcial);
        }

        return costoTotal.setScale(2, RoundingMode.HALF_UP); // Redondea a 2 decimales para moneda
    }

    // Agrega este método al final de FichaTecnicaService:
    public Map<String, BigDecimal> calcularCostoYMargen(Long fichaId, BigDecimal precioVentaSugerido) {
        FichaTecnica ficha = fichaTecnicaRepository.findById(fichaId)
                .orElseThrow(() -> new IllegalArgumentException("La ficha técnica no existe"));

        // 1. Costo de materiales (incluye factor de merma)
        BigDecimal costoMateriales = calcularCostoMateriales(fichaId);

        // 2. Costo de mano de obra (sumatoria de todas las tarifas de las fases configuradas)
        BigDecimal costoManoObra = tarifaDestajoRepository.findAll().stream()
                .filter(t -> t.getFichaTecnica().getId().equals(fichaId))
                .map(TarifaDestajo::getValorPorUnidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Costo Primo (Materiales + Mano de Obra directa)
        BigDecimal costoTotalProduccion = costoMateriales.add(costoManoObra).setScale(2, RoundingMode.HALF_UP);

        // 4. Margen bruto de ganancia estimado
        BigDecimal margenBruto = BigDecimal.ZERO;
        BigDecimal porcentajeMargen = BigDecimal.ZERO;

        if (precioVentaSugerido != null && precioVentaSugerido.compareTo(BigDecimal.ZERO) > 0) {
            margenBruto = precioVentaSugerido.subtract(costoTotalProduccion);
            porcentajeMargen = margenBruto
                    .divide(precioVentaSugerido, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return Map.of(
                "costoMateriales", costoMateriales,
                "costoManoObra", costoManoObra,
                "costoTotalProduccion", costoTotalProduccion,
                "precioVentaSugerido", precioVentaSugerido != null ? precioVentaSugerido : BigDecimal.ZERO,
                "margenBruto", margenBruto,
                "porcentajeMargen", porcentajeMargen
        );
    }
}
