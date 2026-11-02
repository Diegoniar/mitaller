package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.FaseProduccion;
import com.mitaller.mitaller.model.RegistroProduccion;
import com.mitaller.mitaller.model.TarifaDestajo;
import com.mitaller.mitaller.repository.FaseProduccionRepository;
import com.mitaller.mitaller.service.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/produccion")
@RequiredArgsConstructor
public class ProduccionController {

    private final ProduccionService produccionService;
    private final FaseProduccionRepository faseProduccionRepository; // Lo inyectamos directo para simplificar la creación de fases

    // 1. Crear una fase de producción (Ej: Corte, Ensamble)
    @PostMapping("/fases/taller/{empresaId}")
    public ResponseEntity<FaseProduccion> crearFase(@PathVariable Long empresaId, @RequestBody FaseProduccion fase) {

        // ¡Esta es la parte que faltaba! Asignar la empresa usando el ID de la URL
        com.mitaller.mitaller.model.Empresa empresa = new com.mitaller.mitaller.model.Empresa();
        empresa.setId(empresaId);
        fase.setEmpresa(empresa);

        fase.setActivo(true);
        return new ResponseEntity<>(faseProduccionRepository.save(fase), HttpStatus.CREATED);
    }

    // 2. Configurar a cómo se paga esa fase para una prenda específica
    @PostMapping("/tarifas/ficha/{fichaId}/fase/{faseId}")
    public ResponseEntity<TarifaDestajo> configurarTarifa(
            @PathVariable Long fichaId,
            @PathVariable Long faseId,
            @RequestBody TarifaDestajo tarifa) {

        // Simplemente mapeamos los IDs antes de enviar al servicio (asumiendo que existen)
        com.mitaller.mitaller.model.FichaTecnica ficha = new com.mitaller.mitaller.model.FichaTecnica();
        ficha.setId(fichaId);
        tarifa.setFichaTecnica(ficha);

        FaseProduccion fase = new FaseProduccion();
        fase.setId(faseId);
        tarifa.setFaseProduccion(fase);

        return new ResponseEntity<>(produccionService.configurarTarifa(tarifa), HttpStatus.CREATED);
    }

    // 3. Registrar el trabajo del día del operario vinculado a la orden
    @PostMapping("/trabajo/taller/{empresaId}/empleado/{empleadoId}/ficha/{fichaId}/fase/{faseId}/orden/{ordenId}")
    public ResponseEntity<RegistroProduccion> registrarTrabajo(
            @PathVariable Long empresaId,
            @PathVariable Long empleadoId,
            @PathVariable Long fichaId,
            @PathVariable Long faseId,
            @PathVariable Long ordenId,
            @RequestParam Integer cantidad,
            @RequestParam String fecha) {

        LocalDate fechaTrabajo = LocalDate.parse(fecha);
        RegistroProduccion registro = produccionService.registrarTrabajoDiario(
                empresaId, empleadoId, fichaId, faseId, ordenId, cantidad, fechaTrabajo);

        return new ResponseEntity<>(registro, HttpStatus.CREATED);
    }
}