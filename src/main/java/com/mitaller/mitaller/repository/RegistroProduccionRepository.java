package com.mitaller.mitaller.repository;

import com.mitaller.mitaller.model.RegistroProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroProduccionRepository extends JpaRepository<RegistroProduccion, Long> {
    // Útil para liquidar la nómina de un operario en un rango de fechas (ej. de lunes a sábado)
    List<RegistroProduccion> findByEmpleadoIdAndFechaTrabajoBetween(Long empleadoId, LocalDate inicio, LocalDate fin);
}