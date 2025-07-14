package org.example.Controllers;


import org.example.Configuracion.JwtUtil;
import org.example.Models.Usuario;
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

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

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
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        Optional<Usuario> existingUserByUsername = usuarioRepo.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent()) {
            return ResponseEntity.badRequest().body("Error: El nombre de usuario ya está en uso.");
        }

        Optional<Usuario> existingUserByEmail = usuarioRepo.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado.");
        }

        Usuario user = new Usuario();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepo.save(user);

        return ResponseEntity.ok("Usuario registrado con éxito");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest) {
        try {
            System.out.println("Intentando autenticar usuario: " + loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            System.out.println("Autenticación exitosa para: " + loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());

            System.out.println("Token generado: " + jwt);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (Exception e) {
            System.out.println("Error al autenticar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

}
