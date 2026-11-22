// AbonoProveedorRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.AbonoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbonoProveedorRepository extends JpaRepository<AbonoProveedor, Long> {
    List<AbonoProveedor> findByEmpresaIdOrderByFechaDescIdDesc(Long empresaId);
}