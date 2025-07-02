package org.example.Services;

import org.example.Models.Rol;
import org.example.Models.Usuario;
import org.example.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(user)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + user));


        Set<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()

                .map((Rol role) -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toSet());

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                authorities
        );
    }
}
