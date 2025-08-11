package org.example.Services;// src/main/java/org/example/Servicios/RolPermisoService.java


import java.util.List;
import java.util.Optional;

import org.example.Models.RolPermiso;
import org.example.Repositorios.RolPermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolPermisoService {
    @Autowired
    private RolPermisoRepository repo;

    public List<RolPermiso> findAll() {
        return repo.findAll();
    }

    public Optional<RolPermiso> findById(Long id) {
        return repo.findById(id);
    }

    public RolPermiso create(RolPermiso rp) {
        rp.setId(null);
        return repo.save(rp);
    }

    public RolPermiso update(Long id, RolPermiso rp) {
        rp.setId(id);
        return repo.save(rp);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
