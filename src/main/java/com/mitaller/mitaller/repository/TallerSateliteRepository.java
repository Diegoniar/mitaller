package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.TallerSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TallerSateliteRepository extends JpaRepository<TallerSatelite, Long> {
    List<TallerSatelite> findByEmpresaIdAndActivoTrue(Long empresaId);
}