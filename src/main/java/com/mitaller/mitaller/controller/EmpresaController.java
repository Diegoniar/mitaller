package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {
    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<Empresa> crearEmpresa(@RequestBody Empresa empresa){
        Empresa nuevaEmpresa = empresaService.crearEmpresa(empresa);
        return new ResponseEntity<>(nuevaEmpresa, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> listarEmpresas(){
        return ResponseEntity.ok(empresaService.listarEmpresasActivas());
    }
}
