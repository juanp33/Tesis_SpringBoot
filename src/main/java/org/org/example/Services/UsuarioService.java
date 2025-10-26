package org.example.Services;

import java.util.List;
import java.util.Optional;

import org.example.Models.Usuario;
import org.example.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.Models.Rol;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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
        Optional<Usuario> opt = repo.findById(id);
        if (opt.isEmpty()) throw new RuntimeException("Usuario no encontrado");

        Usuario existente = opt.get();

        existente.setUsername(u.getUsername());
        existente.setEmail(u.getEmail());

        // 🔹 Solo encripta si vino una nueva contraseña
        if (u.getPassword() != null && !u.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(u.getPassword()));
        }

        return repo.save(existente);
    }
    public Usuario updateRoles(Long id, Set<Rol> nuevosRoles) {
        Usuario existente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setRoles(nuevosRoles);

        // ✅ No tocar username, password, email ni abogado
        return repo.save(existente);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // ✅ AGREGÁ ESTE MÉTODO
    public Optional<Usuario> findByUsername(String username) {
        return repo.findByUsername(username);
    }
}
