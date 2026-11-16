package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.TallerSatelite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TallerSateliteRepository extends JpaRepository<TallerSatelite, Long> {
    List<TallerSatelite> findByEmpresaId(Long empresaId);
}

