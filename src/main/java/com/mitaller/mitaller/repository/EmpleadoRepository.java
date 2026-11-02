package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // Para buscar a todos los empleados de un taller específico (Multi-tenant)
    List<Empleado> findByEmpresaIdAndActivoTrue(Long empresaId);
}