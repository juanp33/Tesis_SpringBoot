package org.example.Repositorios;

import org.example.Models.Caso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CasoRepository extends JpaRepository<Caso, Long> { List<Caso> findByClienteId(Long clienteId);}