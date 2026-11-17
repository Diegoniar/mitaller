package com.mitaller.mitaller.controller;

import com.mitaller.mitaller.model.Empresa;
import com.mitaller.mitaller.model.Usuario;
import com.mitaller.mitaller.repository.EmpresaRepository;
import com.mitaller.mitaller.repository.UsuarioRepository;
import com.mitaller.mitaller.security.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/registro")
    @Transactional
    public ResponseEntity<?> registrarTaller(@RequestBody RegistroRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo ya está registrado."));
        }

        Empresa empresa = new Empresa();
        empresa.setNit(req.getNit());
        empresa.setRazonSocial(req.getRazonSocial());
        empresa.setCiudad(req.getCiudad() != null ? req.getCiudad() : "Barrancabermeja");
        empresa.setActivo(true);
        empresa = empresaRepository.save(empresa);

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setNombreCompleto(req.getNombreCompleto());
        usuario.setEmail(req.getEmail().toLowerCase().trim());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setRol("ADMIN_TALLER");
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuario);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "empresaId", empresa.getId(),
                "razonSocial", empresa.getRazonSocial(),
                "ciudad", empresa.getCiudad(),
                "nombreUsuario", usuario.getNombreCompleto()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(req.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Correo o contraseña incorrectos."));
        }

        String token = jwtService.generarToken(usuario);
        Empresa emp = usuario.getEmpresa();

        return ResponseEntity.ok(Map.of(
                "token", token,
                "empresaId", emp.getId(),
                "razonSocial", emp.getRazonSocial(),
                "ciudad", emp.getCiudad() != null ? emp.getCiudad() : "Planta Principal",
                "nombreUsuario", usuario.getNombreCompleto()
        ));
    }

    // Acceso rápido para pruebas sobre el taller principal (ID 1)
    @PostMapping("/demo")
    @Transactional
    public ResponseEntity<?> loginDemo() {
        Empresa empresa = empresaRepository.findById(1L)
                .orElseGet(() -> {
                    Empresa nueva = new Empresa();
                    nueva.setNit("900123456-1");
                    nueva.setRazonSocial("Confecciones El Búcaro");
                    nueva.setCiudad("Barrancabermeja");
                    nueva.setActivo(true);
                    return empresaRepository.save(nueva);
                });

        Usuario demo = usuarioRepository.findByEmail("admin@elbucaro.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setEmpresa(empresa);
                    u.setNombreCompleto("Administrador Planta");
                    u.setEmail("admin@elbucaro.com");
                    u.setPassword(passwordEncoder.encode("admin123"));
                    u.setRol("ADMIN_TALLER");
                    u.setActivo(true);
                    return usuarioRepository.save(u);
                });

        String token = jwtService.generarToken(demo);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "empresaId", empresa.getId(),
                "razonSocial", empresa.getRazonSocial(),
                "ciudad", empresa.getCiudad() != null ? empresa.getCiudad() : "Barrancabermeja",
                "nombreUsuario", demo.getNombreCompleto()
        ));
    }

    @Data
    public static class RegistroRequest {
        private String nit;
        private String razonSocial;
        private String ciudad;
        private String nombreCompleto;
        private String email;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}