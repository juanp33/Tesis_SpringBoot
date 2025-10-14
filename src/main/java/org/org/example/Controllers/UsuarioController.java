package org.example.Controllers;

import java.util.List;

import org.example.Models.Usuario;
import org.example.Services.UsuarioService;
import org.org.example.Response.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.Services.AbogadoService;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService service;

    @Autowired
    private AbogadoService abogadoService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> all() {
        List<Usuario> usuarios = service.findAll();

        List<UsuarioDTO> usuariosDTO = usuarios.stream().map(u -> {
            // Buscar abogado que tenga este usuario
            var abogado = abogadoService.findByUsuarioId(u.getId());
            Long abogadoId = abogado.map(a -> a.getId()).orElse(null);

            return new UsuarioDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    abogadoId
            );
        }).toList();

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> one(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario u) {
        Usuario c = service.create(u);
        return ResponseEntity.status(201).body(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario u) {
        if (!service.findById(id).isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(service.update(id, u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}