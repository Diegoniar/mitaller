package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.FichaInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichaInsumoRepository extends JpaRepository<FichaInsumo, Long> {
    // Al extender de JpaRepository, hereda automáticamente save(), findById(), etc.
}