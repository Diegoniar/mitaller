package com.mitaller.mitaller.service;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.FichaInsumo;
import com.mitaller.mitaller.model.FichaTecnica;
import com.mitaller.mitaller.model.Insumo;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.FichaInsumoRepository;
import com.mitaller.mitaller.repository.FichaTecnicaRepository;
import com.mitaller.mitaller.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final EmpresaRepository empresaRepository;
    private final InsumoRepository insumoRepository;
    private final FichaInsumoRepository fichaInsumoRepository;

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
}
