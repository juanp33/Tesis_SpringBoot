package org.example.Controllers;

import org.example.Models.Permiso;
import org.example.Models.Rol;
import org.example.Models.RolPermiso;
import org.example.Repositorios.PermisoRepository;
import org.example.Repositorios.RolPermisoRepository;
import org.example.Repositorios.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolPermisoRepository rolPermisoRepository;

    // Obtener todos los roles y permisos para inicializar la página
    @GetMapping("/roles-y-permisos")
    public ResponseEntity<Map<String, Object>> obtenerRolesYPermisos() {
        List<Rol> roles = rolRepository.findAll();
        List<Permiso>permisos = permisoRepository.findAll();

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("roles", roles);
        resultado.put("permisos", permisos);

        return ResponseEntity.ok(resultado);
    }

    // Actualizar permisos asignados a un rol específico
    @PostMapping("/{rolId}/actualizar-permisos")
    public ResponseEntity<?> actualizarPermisosDeRol(
            @PathVariable Long rolId,
            @RequestBody List<Long> permisosIds) {

        Optional<Rol> rolOpt = rolRepository.findById(rolId);
        if (!rolOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }

        Rol rol = rolOpt.get();

        // Elimina permisos actuales
        rolPermisoRepository.deleteAll(rol.getRolPermisos());
        rol.getRolPermisos().clear();

        // Añade nuevos permisos
        List<Permiso> permisosNuevos = permisoRepository.findAllById(permisosIds);

        Set<RolPermiso> nuevosRolPermisos = permisosNuevos.stream()
                .map(permiso -> {
                    RolPermiso rp = new RolPermiso();
                    rp.setRol(rol);
                    rp.setPermiso(permiso);
                    return rp;
                })
                .collect(Collectors.toSet());

        rol.getRolPermisos().addAll(nuevosRolPermisos);
        rolRepository.save(rol);

        return ResponseEntity.ok("Permisos actualizados con éxito");
    }
}
