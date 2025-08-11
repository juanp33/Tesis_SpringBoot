// src/main/java/org/example/Controllers/RolPermisoController.java
package org.example.Controllers;

import org.example.Models.RolPermiso;
import org.example.Services.RolPermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rolPermisos")
public class RolPermisoController {

    @Autowired
    private RolPermisoService service;

    /** Obtener todos los RolPermiso */
    @GetMapping
    public ResponseEntity<List<RolPermiso>> getAll() {
        List<RolPermiso> list = service.findAll();
        return ResponseEntity.ok(list);
    }

    /** Obtener un RolPermiso por ID */
    @GetMapping("/{id}")
    public ResponseEntity<RolPermiso> getById(@PathVariable Long id) {
        Optional<RolPermiso> opt = service.findById(id);
        return opt
                .map(rp -> ResponseEntity.ok(rp))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Crear un nuevo RolPermiso */
    @PostMapping
    public ResponseEntity<RolPermiso> create(@RequestBody RolPermiso rp) {
        RolPermiso creado = service.create(rp);
        return ResponseEntity.status(201).body(creado);
    }

    /** Actualizar un RolPermiso existente */
    @PutMapping("/{id}")
    public ResponseEntity<RolPermiso> update(
            @PathVariable Long id,
            @RequestBody RolPermiso rp
    ) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        RolPermiso actualizado = service.update(id, rp);
        return ResponseEntity.ok(actualizado);
    }

    /** Borrar un RolPermiso */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
