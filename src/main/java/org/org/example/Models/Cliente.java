package org.example.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.example.Models.Abogado;
import org.example.Models.Caso;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String ci;

    private String email;


    @ManyToMany
    @JoinTable(
            name = "cliente_abogados",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "abogado_id")
    )
    private List<Abogado> abogados;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cliente-casos")
    private List<Caso> casos;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCi() { return ci; }
    public void setCi(String ci) { this.ci = ci; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Abogado> getAbogados() { return abogados; }
    public void setAbogados(List<Abogado> abogados) { this.abogados = abogados; }

    public List<Caso> getCasos() { return casos; }
    public void setCasos(List<Caso> casos) { this.casos = casos; }
}