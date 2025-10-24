// src/main/java/org/example/Services/RolService.java
package org.example.Services;

import java.util.List;
import java.util.Optional;

import org.example.Models.Rol;
import org.example.Models.Usuario;
import org.example.Repositorios.RolRepository;
import org.example.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {

    @Autowired
    private RolRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepository; // 🔹 para acceder a los usuarios

    public List<Rol> findAll() {
        return repo.findAll();
    }

    public Optional<Rol> findById(Long id) {
        return repo.findById(id);
    }

    public Rol create(Rol r) {
        r.setId(null);
        return repo.save(r);
    }

    public Rol update(Long id, Rol r) {
        r.setId(id);
        return repo.save(r);
    }

    // ✅ Elimina el rol y lo quita de todos los usuarios que lo tengan asignado
    public void delete(Long id) {
        Optional<Rol> optRol = repo.findById(id);
        if (!optRol.isPresent()) return;

        Rol rol = optRol.get();

        // 🔹 Buscar todos los usuarios que tengan este rol
        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario u : usuarios) {
            if (u.getRoles() != null && u.getRoles().removeIf(r -> r.getId().equals(id))) {
                usuarioRepository.save(u); // guarda solo si se modificó algo
            }
        }

        // 🔹 Ahora sí eliminar el rol
        repo.deleteById(id);
    }
}
