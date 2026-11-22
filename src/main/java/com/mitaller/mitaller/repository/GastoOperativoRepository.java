// GastoOperativoRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.GastoOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Long> {
    List<GastoOperativo> findByEmpresaIdOrderByFechaDescIdDesc(Long empresaId);
}