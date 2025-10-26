package org.example.Controllers;

import org.example.Configuracion.JwtUtil;
import org.example.Models.Usuario;
import org.example.Models.VerificationCode;
import org.example.Repositorios.UsuarioRepository;
import org.example.Request.JwtRequest;
import org.example.Request.RegisterRequest;
import org.example.Services.UserDetailsServiceImp;
import org.example.Services.EmailService;
import org.example.Services.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TwoFactorService twoFactorService;

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
        if (usuarioRepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "⚠️ El nombre de usuario ya existe."));
        }

        if (usuarioRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "⚠️ El correo electrónico ya está en uso."));
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepo.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "✅ Usuario creado correctamente."));
    }

    // ================== LOGIN (Paso 1: genera OTP) ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Buscar el usuario real desde la DB
            Usuario usuario = usuarioRepo.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Iniciar flujo 2FA (genera código y lo envía)
            VerificationCode vc = twoFactorService.iniciar2FA(usuario);
            String masked = TwoFactorService.maskEmail(usuario.getEmail());

            // Devolvemos transacción pendiente (sin token JWT aún)
            return ResponseEntity.ok(Map.of(
                    "twoFactor", true,
                    "txId", vc.getTxId(),
                    "emailMasked", masked
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "❌ Credenciales inválidas."));
        }
    }

    // ================== VERIFICAR OTP (Paso 2: genera JWT real) ==================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> req) {
        String txId = req.get("txId");
        String code = req.get("code");

        boolean valido = twoFactorService.validarCodigo(txId, code);
        if (!valido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "❌ Código inválido o expirado."));
        }

        Optional<VerificationCode> opt = twoFactorService.getByTxId(txId);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("mensaje", "Transacción inválida."));

        VerificationCode vc = opt.get();
        Usuario usuario = usuarioRepo.findByUsername(vc.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getUsername());
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "id", usuario.getId(),
                "username", usuario.getUsername()
        ));
    }

    // ================== REENVIAR OTP ==================
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> req) {
        String txId = req.get("txId");
        Optional<VerificationCode> oldOpt = twoFactorService.getByTxId(txId);
        if (oldOpt.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("mensaje", "Transacción inválida."));
        }

        VerificationCode old = oldOpt.get();
        Usuario usuario = usuarioRepo.findByUsername(old.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        VerificationCode nuevo = twoFactorService.reemitirCodigo(old, usuario);
        String masked = TwoFactorService.maskEmail(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "twoFactor", true,
                "txId", nuevo.getTxId(),
                "emailMasked", masked
        ));
    }
}
