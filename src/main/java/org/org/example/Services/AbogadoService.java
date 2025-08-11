package org.example.Services;// src/main/java/org/example/Servicios/AbogadoService.java


import java.util.List;
import java.util.Optional;

import org.example.Models.Abogado;
import org.example.Repositorios.AbogadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbogadoService {
    @Autowired
    private AbogadoRepository repo;

    public List<Abogado> findAll() {
        return repo.findAll();
    }

    public Optional<Abogado> findById(Long id) {
        return repo.findById(id);
    }

    public Abogado create(Abogado a) {
        a.setId(null);
        return repo.save(a);
    }

    public Abogado update(Long id, Abogado a) {
        a.setId(id);
        return repo.save(a);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
