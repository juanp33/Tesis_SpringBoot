package org.example.Repositorios;




import org.example.Models.Abogado;

import org.example.Models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;


@RepositoryRestResource
public interface AbogadoRepository extends JpaRepository<Abogado, Long> {

    Optional<Abogado> findByNombre(String nombre);
    Optional<Abogado> findByUsuarioId(Long usuarioId);
}