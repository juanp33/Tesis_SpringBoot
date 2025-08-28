package org.example.Controllers;

import org.example.Models.Caso;
import org.example.Models.ArchivoCaso;
import org.example.Services.CasoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/casos")
public class CasoController {

    private final CasoService casoService;

    public CasoController(CasoService casoService) {
        this.casoService = casoService;
    }

    @PostMapping
    public ResponseEntity<Caso> crearCaso(
            @RequestParam String titulo,
            @RequestParam String tipo,
            @RequestParam String descripcion,
            @RequestParam Long clienteId,
            Authentication auth,
            @RequestParam(required = false) List<MultipartFile> archivos
    ) throws Exception {
        Caso caso = new Caso();
        caso.setTitulo(titulo);
        caso.setTipo(tipo);
        caso.setDescripcion(descripcion);
        caso.setEstado("abierto"); // siempre empieza abierto
        caso.setAbogado(auth.getName()); // abogado = username logueado

        Caso nuevoCaso = casoService.crearCasoParaCliente(caso, clienteId, archivos);
        return ResponseEntity.ok(nuevoCaso);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caso> obtenerCaso(@PathVariable Long id) {
        return ResponseEntity.ok(casoService.obtenerCaso(id));
    }

    @PostMapping("/{id}/archivos")
    public ResponseEntity<ArchivoCaso> subirArchivo(
            @PathVariable Long id,
            @RequestParam MultipartFile archivo
    ) throws Exception {
        return ResponseEntity.ok(casoService.subirArchivo(id, archivo));
    }
}