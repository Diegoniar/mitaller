package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.LiquidacionNomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LiquidacionNominaRepository extends JpaRepository<LiquidacionNomina, Long> {
    // Útil para ver toda la nómina que el taller pagó en una semana específica
    List<LiquidacionNomina> findByEmpresaIdAndFechaFin(Long empresaId, LocalDate fechaFin);
}