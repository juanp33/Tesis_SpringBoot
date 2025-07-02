package org.example.Repositorios;

import org.example.Models.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    // Métodos personalizados si necesitas
}