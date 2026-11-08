package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.FichaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaInsumoRepository extends JpaRepository<FichaInsumo, Long> {
    // Al extender de JpaRepository, hereda automáticamente save(), findById(), etc.
    List<FichaInsumo> findByFichaTecnicaId(Long fichaTecnicaId);
}