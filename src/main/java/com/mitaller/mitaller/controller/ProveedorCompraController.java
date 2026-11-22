package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.*;
import com.mitaller.mitaller.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorCompraController {

    private final ProveedorRepository proveedorRepository;
    private final CompraProveedorRepository compraRepository;
    private final AbonoProveedorRepository abonoRepository;
    private final EmpresaRepository empresaRepository;

    public ProveedorCompraController(
            ProveedorRepository proveedorRepository,
            CompraProveedorRepository compraRepository,
            AbonoProveedorRepository abonoRepository,
            EmpresaRepository empresaRepository) {
        this.proveedorRepository = proveedorRepository;
        this.compraRepository = compraRepository;
        this.abonoRepository = abonoRepository;
        this.empresaRepository = empresaRepository;
    }

    // --- DIRECTORIO DE PROVEEDORES ---

    @GetMapping("/empresa/{empresaId}")
    public List<Proveedor> listarProveedores(@PathVariable Long empresaId) {
        return proveedorRepository.findByEmpresaIdAndActivoTrueOrderByNombreAsc(empresaId);
    }

    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<?> crearProveedor(@PathVariable Long empresaId, @RequestBody Proveedor proveedor) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        proveedor.setEmpresa(empresa);
        BigDecimal saldoInit = proveedor.getSaldoInicial() != null ? proveedor.getSaldoInicial() : BigDecimal.ZERO;
        proveedor.setSaldoInicial(saldoInit);
        proveedor.setSaldoActual(saldoInit);
        proveedor.setActivo(true);
        return ResponseEntity.ok(proveedorRepository.save(proveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarProveedor(@PathVariable Long id) {
        return proveedorRepository.findById(id).map(p -> {
            p.setActivo(false);
            proveedorRepository.save(p);
            return ResponseEntity.ok(Map.of("mensaje", "Proveedor archivado"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- COMPRAS DE INSUMOS Y SERVICIOS ---

    @GetMapping("/compras/empresa/{empresaId}")
    public List<CompraProveedor> listarCompras(@PathVariable Long empresaId) {
        return compraRepository.findByEmpresaIdOrderByFechaDescIdDesc(empresaId);
    }

    @PostMapping("/compras/empresa/{empresaId}")
    @Transactional
    public ResponseEntity<?> registrarCompra(@PathVariable Long empresaId, @RequestBody Map<String, Object> body) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        Long proveedorId = Long.valueOf(body.get("proveedorId").toString());
        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow();

        BigDecimal totalCompra = new BigDecimal(body.get("totalCompra").toString());
        BigDecimal abonoInicial = body.get("abonoInicial") != null && !body.get("abonoInicial").toString().isBlank()
                ? new BigDecimal(body.get("abonoInicial").toString())
                : BigDecimal.ZERO;

        if (totalCompra.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El valor total de la compra debe ser mayor a 0."));
        }

        CompraProveedor compra = new CompraProveedor();
        compra.setEmpresa(empresa);
        compra.setProveedor(proveedor);
        compra.setNumeroFactura(body.getOrDefault("numeroFactura", "").toString());
        compra.setFecha(body.get("fecha") != null ? LocalDate.parse(body.get("fecha").toString()) : LocalDate.now());
        compra.setTipoCompra(body.getOrDefault("tipoCompra", "INSUMOS_TELAS").toString());
        compra.setDescripcion(body.getOrDefault("descripcion", "Compra a proveedor").toString());
        compra.setTotalCompra(totalCompra);
        compra.setAbonoInicial(abonoInicial);
        compra.setMetodoPagoAbono(body.getOrDefault("metodoPagoAbono", "EFECTIVO").toString());

        BigDecimal pendienteCompra = totalCompra.subtract(abonoInicial);
        compra.setSaldoPendienteCompra(pendienteCompra);

        BigDecimal saldoAntes = proveedor.getSaldoActual() != null ? proveedor.getSaldoActual() : BigDecimal.ZERO;
        BigDecimal saldoDespues = saldoAntes.add(pendienteCompra);
        proveedor.setSaldoActual(saldoDespues);
        proveedorRepository.save(proveedor);

        CompraProveedor guardada = compraRepository.save(compra);

        if (abonoInicial.compareTo(BigDecimal.ZERO) > 0) {
            AbonoProveedor abonoInit = new AbonoProveedor();
            abonoInit.setEmpresa(empresa);
            abonoInit.setProveedor(proveedor);
            abonoInit.setFecha(compra.getFecha());
            abonoInit.setMonto(abonoInicial);
            abonoInit.setMetodoPago(compra.getMetodoPagoAbono());
            abonoInit.setObservaciones("Pago inicial factura " + (compra.getNumeroFactura().isBlank() ? "#" + guardada.getId() : compra.getNumeroFactura()));
            abonoInit.setSaldoAnteriorSnapshot(saldoAntes.add(totalCompra));
            abonoInit.setSaldoNuevoSnapshot(saldoDespues);
            abonoRepository.save(abonoInit);
        }

        return ResponseEntity.ok(guardada);
    }

    // --- ABONOS / PAGOS A PROVEEDORES ---

    @GetMapping("/abonos/empresa/{empresaId}")
    public List<AbonoProveedor> listarAbonos(@PathVariable Long empresaId) {
        return abonoRepository.findByEmpresaIdOrderByFechaDescIdDesc(empresaId);
    }

    @PostMapping("/abonos/empresa/{empresaId}")
    @Transactional
    public ResponseEntity<?> registrarAbono(@PathVariable Long empresaId, @RequestBody Map<String, Object> body) {
        Empresa empresa = empresaRepository.findById(empresaId).orElseThrow();
        Long proveedorId = Long.valueOf(body.get("proveedorId").toString());
        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElseThrow();

        BigDecimal monto = new BigDecimal(body.get("monto").toString());
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El monto del abono debe ser mayor a 0."));
        }

        BigDecimal saldoAnterior = proveedor.getSaldoActual() != null ? proveedor.getSaldoActual() : BigDecimal.ZERO;
        BigDecimal saldoNuevo = saldoAnterior.subtract(monto);

        proveedor.setSaldoActual(saldoNuevo);
        proveedorRepository.save(proveedor);

        AbonoProveedor abono = new AbonoProveedor();
        abono.setEmpresa(empresa);
        abono.setProveedor(proveedor);
        abono.setFecha(body.get("fecha") != null ? LocalDate.parse(body.get("fecha").toString()) : LocalDate.now());
        abono.setMonto(monto);
        abono.setMetodoPago(body.getOrDefault("metodoPago", "EFECTIVO").toString());
        abono.setObservaciones(body.getOrDefault("observaciones", "").toString());
        abono.setSaldoAnteriorSnapshot(saldoAnterior);
        abono.setSaldoNuevoSnapshot(saldoNuevo);

        return ResponseEntity.ok(abonoRepository.save(abono));
    }

    @PatchMapping("/{id}/ajustar-saldo")
    public ResponseEntity<?> ajustarSaldoProveedor(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return proveedorRepository.findById(id).map(p -> {
            BigDecimal nuevoSaldo = new BigDecimal(body.get("saldoActual").toString());
            p.setSaldoActual(nuevoSaldo);
            return ResponseEntity.ok(proveedorRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }
}