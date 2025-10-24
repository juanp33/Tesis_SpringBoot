package org.example.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime; // 🔹 NUEVO
import java.util.*;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Abogado abogado;

    // ==============================
    // 🔹 NUEVOS CAMPOS PARA 2FA
    // ==============================
    private String codigo2FA;  // Código temporal enviado al email

    private LocalDateTime codigoExpira; // Fecha y hora de expiración (5 min)

    // ==============================
    // 🔹 GETTERS Y SETTERS
    // ==============================

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Abogado getAbogado() {
        return abogado;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public void setAbogado(Abogado abogado) {
        this.abogado = abogado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ==============================
    // 🔹 NUEVOS GETTERS Y SETTERS
    // ==============================
    public String getCodigo2FA() {
        return codigo2FA;
    }

    public void setCodigo2FA(String codigo2FA) {
        this.codigo2FA = codigo2FA;
    }

    public LocalDateTime getCodigoExpira() {
        return codigoExpira;
    }

    public void setCodigoExpira(LocalDateTime codigoExpira) {
        this.codigoExpira = codigoExpira;
    }
}
