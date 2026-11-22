// AbonoClienteRepository.java
package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.AbonoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbonoClienteRepository extends JpaRepository<AbonoCliente, Long> {
    List<AbonoCliente> findByEmpresaIdOrderByFechaDescIdDesc(Long empresaId);
    List<AbonoCliente> findByClienteIdOrderByFechaDescIdDesc(Long clienteId);
}