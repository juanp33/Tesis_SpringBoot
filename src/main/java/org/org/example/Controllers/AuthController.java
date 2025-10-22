package org.example.Controllers;

import org.example.Configuracion.JwtUtil;
import org.example.Models.Abogado;
import org.example.Models.Usuario;
import org.example.Repositorios.AbogadoRepository;
import org.example.Repositorios.RolRepository;
import org.example.Repositorios.UsuarioRepository;
import org.example.Request.JwtRequest;
import org.example.Request.RegisterRequest;
import org.example.Response.JwtResponse;
import org.example.Services.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AbogadoRepository abogadoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================== VALIDAR TOKEN ==================
    @GetMapping("/validar-token")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no válido o faltante");
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        boolean valido = jwtUtil.isTokenValid(token, userDetails);

        if (!valido) {
            return ResponseEntity.status(401).body("Token expirado o inválido");
        }

        return ResponseEntity.ok("Token válido");
    }

    // ================== REGISTRO ==================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        // 🔹 Validaciones de usuario
        if (usuarioRepo.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", "⚠️ El nombre de usuario ya existe."));
        }

        if (usuarioRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", "⚠️ El correo electrónico ya está en uso."));
        }

        // 🔹 Validación de cédula duplicada
        if (abogadoRepository.existsByCi(request.getCi())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("mensaje", "⚠️ Ya existe un abogado registrado con esa cédula."));
        }

        // 🔹 Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🔹 Crear abogado vinculado al usuario
        Abogado abogado = new Abogado();
        abogado.setNombre(request.getNombre());
        abogado.setApellido(request.getApellido());
        abogado.setCi(request.getCi());
        abogado.setUsuario(usuario);

        usuario.setAbogado(abogado);

        // 🔹 Guardar ambos
        usuarioRepo.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "✅ Usuario y abogado creados correctamente."));
    }

    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "❌ Credenciales inválidas."));
        }
    }
}
