package org.example.Controllers;

import java.util.List;

import org.example.Models.Permiso;
import org.example.Services.PermisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Si no estás usando el CORS global de SecurityConfig, descomenta:
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/permisos")
public class PermisoController {

    @Autowired
    private PermisoService permisoService;

    @GetMapping
    public ResponseEntity<List<Permiso>> getAll() {
        List<Permiso> lista = permisoService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permiso> getById(@PathVariable Long id) {
        return permisoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Permiso> create(@RequestBody Permiso permiso) {
        Permiso creado = permisoService.create(permiso);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permiso> update(
            @PathVariable Long id,
            @RequestBody Permiso permiso
    ) {
        if (!permisoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Permiso actualizado = permisoService.update(id, permiso);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!permisoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        permisoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
