package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.LiquidacionSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LiquidacionSateliteRepository extends JpaRepository<LiquidacionSatelite, Long> {
    List<LiquidacionSatelite> findByEmpresaId(Long empresaId);
}