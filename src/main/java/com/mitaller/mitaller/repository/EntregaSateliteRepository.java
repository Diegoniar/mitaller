package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.EntregaSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EntregaSateliteRepository extends JpaRepository<EntregaSatelite, Long> {
    List<EntregaSatelite> findBySateliteIdAndFechaEntregaBetweenAndLiquidadoFalse(
            Long sateliteId, LocalDate inicio, LocalDate fin);
}