package org.example.Repositorios;

import org.example.Models.Abogado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource
public interface AbogadoRepository extends JpaRepository<Abogado, Long> {

    Optional<Abogado> findByNombre(String nombre);

    // ✅ Método correcto para buscar abogado por el ID del usuario
    Optional<Abogado> findByUsuario_Id(Long usuarioId);

    boolean existsByCi(String ci);
    Optional<Abogado> findByCi(String ci);
}