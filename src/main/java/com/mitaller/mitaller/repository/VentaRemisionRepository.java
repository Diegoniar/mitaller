// VentaRemisionRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.VentaRemision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaRemisionRepository extends JpaRepository<VentaRemision, Long> {
    List<VentaRemision> findByEmpresaIdOrderByFechaDescIdDesc(Long empresaId);
    List<VentaRemision> findByClienteIdOrderByFechaDescIdDesc(Long clienteId);
    long countByEmpresaId(Long empresaId);
}