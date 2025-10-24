package org.example.Repositorios;

import org.example.Models.Abogado;
import org.example.Models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByAbogadosContains(Abogado abogado);


    boolean existsByCi(String ci);
}