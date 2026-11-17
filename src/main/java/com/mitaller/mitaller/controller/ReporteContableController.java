package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.FichaTecnica;
import com.mitaller.mitaller.model.LiquidacionNomina;
import com.mitaller.mitaller.model.LiquidacionSatelite;
import com.mitaller.mitaller.repository.FichaTecnicaRepository;
import com.mitaller.mitaller.repository.LiquidacionNominaRepository;
import com.mitaller.mitaller.repository.LiquidacionSateliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteContableController {

    private final LiquidacionNominaRepository liquidacionNominaRepository;
    private final LiquidacionSateliteRepository liquidacionSateliteRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;

    // 1. Exportar Planilla de Nómina (CSV compatible con Excel)
    @GetMapping("/nomina/empresa/{empresaId}")
    public ResponseEntity<byte[]> exportarNominaCsv(@PathVariable Long empresaId) {
        List<LiquidacionNomina> lista = liquidacionNominaRepository.findAll().stream()
                .filter(l -> l.getEmpresa() != null && l.getEmpresa().getId().equals(empresaId))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("ID;Trabajador;Documento;Tipo Contrato;Inicio;Fin;Base Proporcional;Destajo y Dominical;Aux Transporte;Deducciones 8%;Provisiones Empleador;Neto Pagado\n");

        for (LiquidacionNomina l : lista) {
            sb.append(l.getId()).append(";")
                    .append(limpiar(l.getEmpleado().getNombreCompleto())).append(";")
                    .append(limpiar(l.getEmpleado().getDocumentoIdentidad())).append(";")
                    .append(l.getEmpleado().getTipoVinculacion().name()).append(";")
                    .append(l.getFechaInicio()).append(";")
                    .append(l.getFechaFin()).append(";")
                    .append(l.getSalarioBaseProporcional()).append(";")
                    .append(l.getTotalDestajo()).append(";")
                    .append(l.getAuxilioTransporte()).append(";")
                    .append(l.getDeducciones()).append(";")
                    .append(l.getProvisiones()).append(";")
                    .append(l.getNetoAPagar()).append("\n");
        }

        return generarRespuestaCsv(sb.toString(), "planilla_nomina_empresa_" + empresaId + ".csv");
    }

    // 2. Exportar Liquidaciones de Talleres Satélites
    @GetMapping("/satelites/empresa/{empresaId}")
    public ResponseEntity<byte[]> exportarSatelitesCsv(@PathVariable Long empresaId) {
        List<LiquidacionSatelite> lista = liquidacionSateliteRepository.findAll().stream()
                .filter(l -> l.getEmpresa() != null && l.getEmpresa().getId().equals(empresaId))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("ID;Taller Satelite;Encargado;NIT/CC;Inicio;Fin;Prendas Confeccionadas;Subtotal Bruto;Descuentos;Neto a Pagar\n");

        for (LiquidacionSatelite l : lista) {
            java.math.BigDecimal descuentos = (l.getSubtotalBruto() != null && l.getNetoAPagar() != null)
                    ? l.getSubtotalBruto().subtract(l.getNetoAPagar())
                    : java.math.BigDecimal.ZERO;

            sb.append(l.getId()).append(";")
                    .append(limpiar(l.getSatelite().getNombreTaller())).append(";")
                    .append(limpiar(l.getSatelite().getNombreEncargado())).append(";")
                    .append(limpiar(l.getSatelite().getNitOIdentificacion())).append(";")
                    .append(l.getFechaInicio()).append(";")
                    .append(l.getFechaFin()).append(";")
                    .append(l.getTotalPrendasConfeccionadas()).append(";")
                    .append(l.getSubtotalBruto()).append(";")
                    .append(descuentos).append(";")
                    .append(l.getNetoAPagar()).append("\n");
        }

        return generarRespuestaCsv(sb.toString(), "liquidaciones_satelites_empresa_" + empresaId + ".csv");
    }

    // 3. Exportar Catálogo de Fichas Técnicas
    @GetMapping("/fichas/empresa/{empresaId}")
    public ResponseEntity<byte[]> exportarFichasCsv(@PathVariable Long empresaId) {
        List<FichaTecnica> lista = fichaTecnicaRepository.findAll().stream()
                .filter(f -> f.getEmpresa() != null && f.getEmpresa().getId().equals(empresaId))
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("ID;Referencia;Descripcion;Tallas\n");

        for (FichaTecnica f : lista) {
            sb.append(f.getId()).append(";")
                    .append(limpiar(f.getReferencia())).append(";")
                    .append(limpiar(f.getDescripcion())).append(";")
                    .append(limpiar(f.getTallas())).append("\n");
        }

        return generarRespuestaCsv(sb.toString(), "catalogo_prendas_empresa_" + empresaId + ".csv");
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.replace(";", ",").replace("\n", " ").trim();
    }

    private ResponseEntity<byte[]> generarRespuestaCsv(String contenido, String nombreArchivo) {
        // BOM UTF-8 para que Microsoft Excel reconozca tildes y ñ automáticamente
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] datos = contenido.getBytes(StandardCharsets.UTF_8);
        byte[] resultado = new byte[bom.length + datos.length];
        System.arraycopy(bom, 0, resultado, 0, bom.length);
        System.arraycopy(datos, 0, resultado, bom.length, datos.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(resultado);
    }
}