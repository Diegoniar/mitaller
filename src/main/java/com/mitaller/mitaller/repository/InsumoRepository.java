package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByEmpresaIdAndActivoTrue(Long empresaId);
}