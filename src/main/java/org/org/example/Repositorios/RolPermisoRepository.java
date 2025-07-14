package org.example.Repositorios;

import org.example.Models.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

}