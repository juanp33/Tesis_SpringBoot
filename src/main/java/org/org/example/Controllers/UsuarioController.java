package org.example.Controllers;

import java.util.*;

import org.example.Models.Rol;
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
            var abogado = abogadoService.findByUsuarioId(u.getId());
            Long abogadoId = abogado.map(a -> a.getId()).orElse(null);
            String abogadoNombre = abogado.map(a -> a.getNombre()).orElse("");
            String abogadoApellido = abogado.map(a -> a.getApellido()).orElse("");

            return new UsuarioDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    abogadoId,
                    abogadoNombre,
                    abogadoApellido
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

    @PutMapping("/{id}/roles")
    public ResponseEntity<Usuario> updateRoles(
            @PathVariable Long id,
            @RequestBody List<Long> nuevosRolesIds) {

        Optional<Usuario> optUsuario = service.findById(id);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = optUsuario.get();

        // Crear objetos Rol con solo el ID
        Set<Rol> nuevosRoles = nuevosRolesIds.stream()
                .map(rolId -> {
                    org.example.Models.Rol r = new org.example.Models.Rol();
                    r.setId(rolId);
                    return r;
                })
                .collect(java.util.stream.Collectors.toSet());

        usuario.setRoles(nuevosRoles);

        // Guardar cambios
        Usuario actualizado = service.update(id, usuario);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<Set<Rol>> getRolesByUsuario(@PathVariable Long id) {
        Optional<Usuario> opt = service.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(opt.get().getRoles());
    }
    @GetMapping("/{identificador}/permisos")
    public ResponseEntity<List<String>> getPermisosPorUsuario(@PathVariable String identificador) {
        Optional<Usuario> usuarioOpt;

        // Si el parámetro es numérico, busca por ID; si no, por username
        if (identificador.matches("\\d+")) {
            usuarioOpt = service.findById(Long.parseLong(identificador));
        } else {
            usuarioOpt = service.findByUsername(identificador);
        }

        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        Usuario usuario = usuarioOpt.get();
        Set<String> permisos = new HashSet<>();

        usuario.getRoles().forEach(rol -> {
            rol.getRolPermisos().forEach(rp -> {
                if (rp.getPermiso() != null && rp.getPermiso().getNombre() != null) {
                    permisos.add(rp.getPermiso().getNombre());
                }
            });
        });

        return ResponseEntity.ok(new ArrayList<>(permisos));
    }

}