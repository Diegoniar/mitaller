// ProveedorRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByEmpresaIdAndActivoTrueOrderByNombreAsc(Long empresaId);
}