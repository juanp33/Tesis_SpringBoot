package org.example.Services;

import java.util.List;
import java.util.Optional;

import org.example.Models.Usuario;
import org.example.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public List<Usuario> findAll() {
        return repo.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return repo.findById(id);
    }

    public Usuario create(Usuario u) {
        u.setId(null);
        return repo.save(u);
    }

    public Usuario update(Long id, Usuario u) {
        u.setId(id);
        return repo.save(u);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // ✅ AGREGÁ ESTE MÉTODO
    public Optional<Usuario> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
