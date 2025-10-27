package org.example.Controllers;

import org.example.Models.Caso;
import org.example.Models.ArchivoCaso;
import org.example.Services.CasoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.*;

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
    public ResponseEntity<Caso> obtenerCaso(
            @PathVariable Long id,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                casoService.obtenerCasoSeguro(id, auth.getName())
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<Caso> actualizarCaso(
            @PathVariable Long id,
            @RequestBody Caso casoActualizado,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                casoService.actualizarCasoSeguro(id, casoActualizado, auth.getName())
        );
    }


    @PutMapping("/{id}/estado")
    public ResponseEntity<Caso> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoDTO estadoDto,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                casoService.cambiarEstadoSeguro(id, estadoDto.getEstado(), auth.getName())
        );
    }


    @PostMapping("/{id}/archivos")
    public ResponseEntity<ArchivoCaso> subirArchivo(
            @PathVariable Long id,
            @RequestParam MultipartFile archivo,
            Authentication auth
    ) throws Exception {
        return ResponseEntity.ok(casoService.subirArchivoSeguro(id, archivo, auth.getName()));
    }


    @DeleteMapping("/{id}/archivos/{archivoId}")
    public ResponseEntity<Caso> eliminarArchivo(
            @PathVariable Long id,
            @PathVariable Long archivoId,
            Authentication auth
    ) throws Exception {
        return ResponseEntity.ok(
                casoService.eliminarArchivoSeguro(id, archivoId, auth.getName())
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCaso(
            @PathVariable Long id,
            Authentication auth
    ) {
        casoService.eliminarCasoSeguro(id, auth.getName());
        return ResponseEntity.noContent().build();
    }


    public static class EstadoDTO {
        private String estado;
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarCasoCompleto(
            @PathVariable Long id,
            Authentication auth
    ) throws IOException {
        File zip = casoService.generarZipDeCaso(id, auth.getName());

        InputStreamResource resource = new InputStreamResource(new FileInputStream(zip));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zip.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zip.length())
                .body(resource);
    }
}
