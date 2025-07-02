package org.example.Models;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolPermiso> rolPermisos = new HashSet<>();
}
