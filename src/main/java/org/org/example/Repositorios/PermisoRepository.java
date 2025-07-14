package org.example.Repositorios;

import org.example.Models.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    // Métodos personalizados si necesitas
}