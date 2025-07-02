package org.example.Controllers;

import org.example.Configuracion.JwtUtil;
import org.example.Models.Usuario;

import org.example.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @GetMapping("/me")
    public ResponseEntity<?> obtenerUsuarioDesdeToken(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token no válido o faltante");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.isTokenValid(token, userDetails)) {
            return ResponseEntity.status(401).body("Token no válido o expirado");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(usuario);
    }
}
