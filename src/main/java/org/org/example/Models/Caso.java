package org.example.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "casos")
public class Caso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;       // Ej: "Pérez vs Gómez"
    private String tipo;         // civil, penal, laboral, etc.

    private String abogado;      // abogado asignado
    private String estado;       // abierto, cerrado, en proceso

    @Column(length = 2000)       // permite textos más largos
    private String descripcion;  // descripción del caso

    private LocalDateTime fechaCreacion = LocalDateTime.now();


    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("caso-archivos")
    private List<ArchivoCaso> archivos;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonBackReference("cliente-casos")
    private Cliente cliente;

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAbogado() {
        return abogado;
    }

    public void setAbogado(String abogado) {
        this.abogado = abogado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ArchivoCaso> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoCaso> archivos) {
        this.archivos = archivos;
    }
}
