package org.example.Services;

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
        if (repo.existsByCi(a.getCi())) {
            throw new IllegalArgumentException("duplicado_ci");
        }
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

    // ✅ Buscar abogado por ID de usuario
    public Optional<Abogado> findByUsuarioId(Long usuarioId) {
        return repo.findByUsuario_Id(usuarioId);
    }

    // ✅ Buscar abogado por nombre de usuario del token JWT
    public Abogado findByUsuarioUsername(String username) {
        return repo.findAll().stream()
                .filter(a -> a.getUsuario() != null &&
                        a.getUsuario().getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}
