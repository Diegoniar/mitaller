package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.FaseProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaseProduccionRepository extends JpaRepository<FaseProduccion, Long> {
    List<FaseProduccion> findByEmpresaIdAndActivoTrueOrderByOrdenFaseAsc(Long empresaId);
}