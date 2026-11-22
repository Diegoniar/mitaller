package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comercial")
@CrossOrigin(origins = "*")
public class ComercialCarteraController {

    private final ClienteRepository clienteRepository;
    private final VentaRemisionRepository ventaRepository;
    private final AbonoClienteRepository abonoRepository;
    private final EmpresaRepository empresaRepository;
    private final FichaTecnicaRepository fichaRepository;

    public ComercialCarteraController(
            ClienteRepository clienteRepository,
            VentaRemisionRepository ventaRepository,
            AbonoClienteRepository abonoRepository,
            EmpresaRepository empresaRepository,
            FichaTecnicaRepository fichaRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.abonoRepository = abonoRepository;
        this.empresaRepository = empresaRepository;
        this.fichaRepository = fichaRepository;
    }

    // --- CLIENTES Y CARTERA ---

    @GetMapping("/clientes/empresa/{empresaId}")
    public List<Cliente> listarClientes(@PathVariable Long empresaId) {
        return clienteRepository.findByEmpresaIdAndActivoTrueOrderByNombreAsc(empresaId);
    }

    @PostMapping("/clientes/empresa/{empresaId}")
    public ResponseEntity<?> crearCliente(@PathVariable Long empresaId, @RequestBody Cliente cliente) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        cliente.setEmpresa(empresa);
        BigDecimal saldoInit = cliente.getSaldoInicial() != null ? cliente.getSaldoInicial() : BigDecimal.ZERO;
        cliente.setSaldoInicial(saldoInit);
        cliente.setSaldoActual(saldoInit);
        cliente.setActivo(true);
        return ResponseEntity.ok(clienteRepository.save(cliente));
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id) {
        return clienteRepository.findById(id).map(c -> {
            c.setActivo(false);
            clienteRepository.save(c);
            return ResponseEntity.ok(Map.of("mensaje", "Cliente archivado"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- RECIBOS DE VENTA / REMISIONES ---

    @GetMapping("/ventas/empresa/{empresaId}")
    public List<VentaRemision> listarVentas(@PathVariable Long empresaId) {
        return ventaRepository.findByEmpresaIdOrderByFechaDescIdDesc(empresaId);
    }

    @PostMapping("/ventas/empresa/{empresaId}")
    @Transactional
    public ResponseEntity<?> registrarVenta(@PathVariable Long empresaId, @RequestBody Map<String, Object> body) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();

        VentaRemision venta = new VentaRemision();
        venta.setEmpresa(empresa);
        venta.setCliente(cliente);

        long consecutivo = ventaRepository.countByEmpresaId(empresaId) + 1;
        String numRem = body.get("numeroRemision") != null && !body.get("numeroRemision").toString().isBlank()
                ? body.get("numeroRemision").toString()
                : String.format("REM-%d-%04d", LocalDate.now().getYear(), consecutivo);
        venta.setNumeroRemision(numRem);

        venta.setFecha(body.get("fecha") != null ? LocalDate.parse(body.get("fecha").toString()) : LocalDate.now());
        venta.setObservaciones(body.get("observaciones") != null ? body.get("observaciones").toString() : "");
        venta.setMetodoPagoAbono(body.get("metodoPagoAbono") != null ? body.get("metodoPagoAbono").toString() : "EFECTIVO");

        BigDecimal abonoInicial = body.get("abonoInicial") != null
                ? new BigDecimal(body.get("abonoInicial").toString())
                : BigDecimal.ZERO;
        venta.setAbonoInicial(abonoInicial);

        @SuppressWarnings("unchecked")
        List itemsRaw = (List) body.get("items");
        if (itemsRaw == null || itemsRaw.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Agrega al menos una prenda al recibo de venta."));
        }

        BigDecimal totalVenta = BigDecimal.ZERO;
        BigDecimal totalCosto = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (Object obj : itemsRaw) {
            @SuppressWarnings("unchecked")
            Map<String, Object> item = (Map<String, Object>) obj;
            DetalleVenta det = new DetalleVenta();
            det.setEmpresa(empresa);
            det.setVentaRemision(venta);

            int cantidad = Integer.parseInt(item.get("cantidad").toString());
            BigDecimal precioUnit = new BigDecimal(item.get("precioUnitario").toString());
            BigDecimal costoUnit = BigDecimal.ZERO;

            if (item.get("costoUnitario") != null && !item.get("costoUnitario").toString().isBlank()) {
                costoUnit = new BigDecimal(item.get("costoUnitario").toString());
            }

            if (item.get("fichaTecnicaId") != null && !item.get("fichaTecnicaId").toString().isBlank()) {
                Long fichaId = Long.valueOf(item.get("fichaTecnicaId").toString());
                FichaTecnica ficha = fichaRepository.findById(fichaId).orElse(null);
                if (ficha != null) {
                    det.setFichaTecnica(ficha);
                    det.setDescripcionPrenda(ficha.getReferencia() + " - " + ficha.getDescripcion());
                }
            }

            if (det.getDescripcionPrenda() == null) {
                det.setDescripcionPrenda(item.getOrDefault("descripcion", "Prenda confeccionada").toString());
            }

            det.setCantidad(cantidad);
            det.setPrecioUnitarioVenta(precioUnit);
            det.setCostoUnitarioEstimado(costoUnit);

            BigDecimal subtotal = precioUnit.multiply(BigDecimal.valueOf(cantidad));
            det.setSubtotal(subtotal);

            totalVenta = totalVenta.add(subtotal);
            totalCosto = totalCosto.add(costoUnit.multiply(BigDecimal.valueOf(cantidad)));
            detalles.add(det);
        }

        venta.setDetalles(detalles);
        venta.setTotalVenta(totalVenta);
        venta.setCostoTotalFabricacion(totalCosto);
        venta.setUtilidadNetaVenta(totalVenta.subtract(totalCosto));

        BigDecimal pendienteEstaVenta = totalVenta.subtract(abonoInicial);
        venta.setSaldoPendienteVenta(pendienteEstaVenta);

        // Actualizar saldo vivo del cliente
        BigDecimal saldoAntes = cliente.getSaldoActual() != null ? cliente.getSaldoActual() : BigDecimal.ZERO;
        BigDecimal saldoDespues = saldoAntes.add(pendienteEstaVenta);
        cliente.setSaldoActual(saldoDespues);
        clienteRepository.save(cliente);

        VentaRemision guardada = ventaRepository.save(venta);

        // Si hubo abono inicial, dejar trazabilidad en historial de abonos
        if (abonoInicial.compareTo(BigDecimal.ZERO) > 0) {
            AbonoCliente abonoInit = new AbonoCliente();
            abonoInit.setEmpresa(empresa);
            abonoInit.setCliente(cliente);
            abonoInit.setFecha(venta.getFecha());
            abonoInit.setMonto(abonoInicial);
            abonoInit.setMetodoPago(venta.getMetodoPagoAbono());
            abonoInit.setObservaciones("Abono inicial en recibo " + venta.getNumeroRemision());
            abonoInit.setSaldoAnteriorSnapshot(saldoAntes.add(totalVenta));
            abonoInit.setSaldoNuevoSnapshot(saldoDespues);
            abonoRepository.save(abonoInit);
        }

        return ResponseEntity.ok(guardada);
    }

    // --- ABONOS DIARIOS DE CLIENTES ---

    @GetMapping("/abonos/empresa/{empresaId}")
    public List<AbonoCliente> listarAbonos(@PathVariable Long empresaId) {
        return abonoRepository.findByEmpresaIdOrderByFechaDescIdDesc(empresaId);
    }

    @PostMapping("/abonos/empresa/{empresaId}")
    @Transactional
    public ResponseEntity<?> registrarAbono(@PathVariable Long empresaId, @RequestBody Map<String, Object> body) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();

        BigDecimal monto = new BigDecimal(body.get("monto").toString());
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El monto del abono debe ser mayor a 0."));
        }

        BigDecimal saldoAnterior = cliente.getSaldoActual() != null ? cliente.getSaldoActual() : BigDecimal.ZERO;
        BigDecimal saldoNuevo = saldoAnterior.subtract(monto);

        cliente.setSaldoActual(saldoNuevo);
        clienteRepository.save(cliente);

        AbonoCliente abono = new AbonoCliente();
        abono.setEmpresa(empresa);
        abono.setCliente(cliente);
        abono.setFecha(body.get("fecha") != null ? LocalDate.parse(body.get("fecha").toString()) : LocalDate.now());
        abono.setMonto(monto);
        abono.setMetodoPago(body.getOrDefault("metodoPago", "EFECTIVO").toString());
        abono.setObservaciones(body.getOrDefault("observaciones", "").toString());
        abono.setSaldoAnteriorSnapshot(saldoAnterior);
        abono.setSaldoNuevoSnapshot(saldoNuevo);

        return ResponseEntity.ok(abonoRepository.save(abono));
    }
}