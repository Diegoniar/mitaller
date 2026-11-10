package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.EntregaSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntregaSateliteRepository extends JpaRepository<EntregaSatelite, Long> {

    // Método requerido por SateliteService.java
    List<EntregaSatelite> findBySateliteIdAndFechaEntregaBetweenAndLiquidadoFalse(
            Long sateliteId,
            LocalDate inicio,
            LocalDate fin
    );

    // Método requerido por TallerSateliteService.java (multi-tenant con empresaId)
    List<EntregaSatelite> findByEmpresaIdAndSateliteIdAndLiquidadaFalseAndFechaEntregaBetween(
            Long empresaId,
            Long sateliteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

}