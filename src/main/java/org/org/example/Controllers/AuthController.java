package org.example.Controllers;

import org.example.Configuracion.JwtUtil;
import org.example.Models.Abogado;
import org.example.Models.Usuario;
import org.example.Repositorios.AbogadoRepository;
import org.example.Repositorios.RolRepository;
import org.example.Repositorios.UsuarioRepository;
import org.example.Request.JwtRequest;
import org.example.Request.RegisterRequest;
import org.example.Services.UserDetailsServiceImp;
import org.example.Services.UsuarioService;
import org.example.Utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

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

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MailService mailService;

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

        // ✅ Obtener UserDetails y pasarlo al validador
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
        if (usuarioRepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "⚠️ El nombre de usuario ya existe."));
        }

        if (usuarioRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "⚠️ El correo electrónico ya está en uso."));
        }

        if (abogadoRepository.existsByCi(request.getCi())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "⚠️ Ya existe un abogado registrado con esa cédula."));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        Abogado abogado = new Abogado();
        abogado.setNombre(request.getNombre());
        abogado.setApellido(request.getApellido());
        abogado.setCi(request.getCi());
        abogado.setUsuario(usuario);
        usuario.setAbogado(abogado);

        usuarioRepo.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "✅ Usuario y abogado creados correctamente."));
    }

    // ================== LOGIN CON 2FA ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            Usuario usuario = usuarioRepo.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // 🔹 Generar código 2FA de 6 dígitos
            String codigo = String.format("%06d", new Random().nextInt(999999));
            usuario.setCodigo2FA(codigo);
            usuario.setCodigoExpira(LocalDateTime.now().plusMinutes(5));
            usuarioRepo.save(usuario);

            // 🔹 Enviar email
            mailService.enviarCodigo(usuario.getEmail(), codigo);

            return ResponseEntity.ok(Map.of(
                    "status", "2FA_REQUIRED",
                    "mensaje", "Se envió un código de verificación a tu correo."
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "❌ Credenciales inválidas."));
        }
    }

    // ================== VERIFICAR 2FA ==================
    @PostMapping("/verificar-2fa")
    public ResponseEntity<?> verificar2FA(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String codigoIngresado = body.get("codigo");

        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 🔹 Validar código y expiración
        if (usuario.getCodigo2FA() == null
                || usuario.getCodigoExpira() == null
                || usuario.getCodigoExpira().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "El código expiró o es inválido. Iniciá sesión nuevamente."));
        }

        if (!usuario.getCodigo2FA().equals(codigoIngresado)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Código incorrecto."));
        }

        // 🔹 Si el código es correcto, limpiamos los campos y generamos el JWT
        usuario.setCodigo2FA(null);
        usuario.setCodigoExpira(null);
        usuarioRepo.save(usuario);

        // ✅ Generar el token con UserDetails (seguridad)
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        // ✅ Extraer permisos del usuario
        var permisos = usuario.getRoles().stream()
                .flatMap(r -> r.getRolPermisos().stream()
                        .map(rp -> rp.getPermiso().getNombre()))
                .distinct()
                .toList();

        // 🔹 Devolver todo: token, username, id y permisos
        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "id", usuario.getId(),
                "username", usuario.getUsername(),
                "permisos", permisos
        ));
    }

}
