package org.example.Repositorios;

import org.example.Models.ArchivoCaso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoCasoRepository extends JpaRepository<ArchivoCaso, Long> { List<ArchivoCaso> findByCasoId(Long casoId);}