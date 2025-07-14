package org.example.Repositorios;




import org.example.Models.Abogado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



@RepositoryRestResource
public interface AbogadoRepository extends JpaRepository<Abogado, Long> {

}