package org.example.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.example.Models.Caso;

import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_caso")
public class ArchivoCaso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreArchivo;
    private String rutaArchivo; // ruta en disco o S3
    private LocalDateTime fechaSubida = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "caso_id")
    @JsonBackReference("caso-archivos")
    private Caso caso;
    public Caso getCaso() {
        return caso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public void setCaso(Caso caso) {
        this.caso = caso;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }



    // getters y setters
}
