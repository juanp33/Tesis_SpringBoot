package org.example.Repositorios;

import org.example.Models.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

}