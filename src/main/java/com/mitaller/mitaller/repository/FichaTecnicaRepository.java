package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {
    List<FichaTecnica> findByEmpresaIdAndActivoTrue(Long empresaId);

}