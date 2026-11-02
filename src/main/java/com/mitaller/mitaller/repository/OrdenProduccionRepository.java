package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.EstadoOrden;
import com.mitaller.mitaller.model.OrdenProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Long> {
    Optional<OrdenProduccion> findByNumeroOrden(String numeroOrden);
    List<OrdenProduccion> findByEmpresaIdAndEstado(Long empresaId, EstadoOrden estado);
}