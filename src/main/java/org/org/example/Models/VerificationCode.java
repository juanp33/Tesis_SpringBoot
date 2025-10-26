// src/main/java/org/example/Models/VerificationCode.java
package org.example.Models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private String username;
    private String code;           // p.ej. "123456"
    private String txId;           // identificador de la transacción 2FA
    private Instant expiresAt;     // vencimiento
    private boolean used = false;
    private int attempts = 0;      // para limitar intentos
    private String purpose;        // "LOGIN_2FA"

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTxId() { return txId; }
    public void setTxId(String txId) { this.txId = txId; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
