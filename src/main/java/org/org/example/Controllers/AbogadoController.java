package org.example.Controllers;

import java.util.List;

import org.example.Models.Abogado;
import org.example.Services.AbogadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/abogados")
public class AbogadoController {
    @Autowired
    private AbogadoService service;

    @GetMapping
    public ResponseEntity<List<Abogado>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abogado> one(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Abogado> create(@RequestBody Abogado a) {
        Abogado c = service.create(a);
        return ResponseEntity.status(201).body(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Abogado> update(@PathVariable Long id, @RequestBody Abogado a) {
        if (!service.findById(id).isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(service.update(id, a));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
