package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.TarifaDestajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaDestajoRepository extends JpaRepository<TarifaDestajo, Long> {
    // Busca cuánto se paga por hacer una fase específica de una prenda específica
    Optional<TarifaDestajo> findByFichaTecnicaIdAndFaseProduccionId(Long fichaTecnicaId, Long faseProduccionId);
}