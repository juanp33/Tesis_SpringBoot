package org.example.Services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.example.Models.Usuario;
import org.example.Models.VerificationCode;
import org.example.Repositorios.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    private static final Duration EXPIRATION = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 5;

    @Autowired private VerificationCodeRepository repo;
    @Autowired private EmailService emailService;

    private String generarCodigo6() {
        SecureRandom r = new SecureRandom();
        int n = 100000 + r.nextInt(900000); // 6 dígitos
        return String.valueOf(n);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "correo";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        String visible = local.length() <= 2 ? local : local.substring(0, 2);
        return visible + "****@" + domain;
    }

    public VerificationCode iniciar2FA(Usuario usuario) {
        String code = generarCodigo6();
        String txId = UUID.randomUUID().toString();
        VerificationCode vc = new VerificationCode();
        vc.setUsuarioId(usuario.getId());
        vc.setUsername(usuario.getUsername());
        vc.setCode(code);
        vc.setTxId(txId);
        vc.setPurpose("LOGIN_2FA");
        vc.setExpiresAt(Instant.now().plus(EXPIRATION));
        vc.setUsed(false);
        vc.setAttempts(0);
        repo.save(vc);

        // enviar el email
        if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
            emailService.enviarTokenVerificacion(usuario.getEmail(), code);
        }
        return vc;
    }

    public Optional<VerificationCode> getByTxId(String txId) {
        return repo.findByTxId(txId);
    }

    public boolean validarCodigo(String txId, String code) {
        Optional<VerificationCode> opt = repo.findByTxId(txId);
        if (opt.isEmpty()) return false;

        VerificationCode vc = opt.get();
        if (vc.isUsed()) return false;
        if (vc.getAttempts() >= MAX_ATTEMPTS) return false;
        if (Instant.now().isAfter(vc.getExpiresAt())) return false;

        vc.setAttempts(vc.getAttempts() + 1);

        if (vc.getCode().equals(code)) {
            vc.setUsed(true);
            repo.save(vc);
            return true;
        } else {
            repo.save(vc);
            return false;
        }
    }

    public VerificationCode reemitirCodigo(VerificationCode old, Usuario usuario) {
        // invalidar anterior
        old.setUsed(true);
        repo.save(old);
        // crear nuevo
        return iniciar2FA(usuario);
    }
}
