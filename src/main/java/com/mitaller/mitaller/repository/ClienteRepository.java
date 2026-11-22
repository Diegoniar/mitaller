// ClienteRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByEmpresaIdAndActivoTrueOrderByNombreAsc(Long empresaId);
}