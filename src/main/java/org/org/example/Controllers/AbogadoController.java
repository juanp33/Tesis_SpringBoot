package org.example.Controllers;

import java.util.List;
import java.util.Map;

import org.example.Models.Abogado;
import org.example.Services.AbogadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/abogados")
public class AbogadoController {

    @Autowired
    private AbogadoService service;

    // 🔹 Listar todos los abogados
    @GetMapping
    public ResponseEntity<List<Abogado>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    // 🔹 Obtener abogado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Abogado> one(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Crear nuevo abogado (con validación de CI)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Abogado a) {
        try {
            Abogado nuevo = service.create(a);
            return ResponseEntity.status(201).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "mensaje", "⚠️ Ya existe un abogado registrado con esa cédula."
            ));
        }
    }

    // 🔹 Actualizar abogado
    @PutMapping("/{id}")
    public ResponseEntity<Abogado> update(@PathVariable Long id, @RequestBody Abogado a) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.update(id, a));
    }

    // 🔹 Eliminar abogado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Obtener abogado actual (usando token)
    @GetMapping("/me")
    public ResponseEntity<?> getAbogadoActual(Authentication auth) {
        try {
            String username = auth.getName();
            Abogado abogado = service.findByUsuarioUsername(username);

            if (abogado == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "mensaje", "❌ No se encontró abogado asociado al usuario autenticado."
                ));
            }

            return ResponseEntity.ok(abogado);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "mensaje", "❌ Error al obtener el abogado actual.",
                    "detalle", e.getMessage()
            ));
        }
    }
}
