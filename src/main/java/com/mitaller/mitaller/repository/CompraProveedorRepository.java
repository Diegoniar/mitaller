// CompraProveedorRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.CompraProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompraProveedorRepository extends JpaRepository<CompraProveedor, Long> {
    List<CompraProveedor> findByEmpresaIdOrderByFechaDescIdDesc(Long empresaId);
}