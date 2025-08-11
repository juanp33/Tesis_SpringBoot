package org.example.Services;

import java.util.List;
import java.util.Optional;

import org.example.Models.Permiso;
import org.example.Repositorios.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepo;

    public List<Permiso> findAll() {
        return permisoRepo.findAll();
    }

    public Optional<Permiso> findById(Long id) {
        return permisoRepo.findById(id);
    }

    public Permiso create(Permiso permiso) {
        permiso.setId(null);    // asegúrate de que es un alta nueva
        return permisoRepo.save(permiso);
    }

    public Permiso update(Long id, Permiso permiso) {
        permiso.setId(id);
        return permisoRepo.save(permiso);
    }

    public void delete(Long id) {
        permisoRepo.deleteById(id);
    }
}