// src/main/java/org/example/Servicios/RolService.java
package org.example.Services;

import java.util.List;
import java.util.Optional;

import org.example.Models.Rol;
import org.example.Repositorios.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {
    @Autowired
    private RolRepository repo;

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

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
