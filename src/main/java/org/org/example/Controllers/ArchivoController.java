package org.example.Controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @GetMapping("/{casoId}/{nombreArchivo}")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable Long casoId,
            @PathVariable String nombreArchivo,
            Authentication authentication) throws MalformedURLException {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).build();
        }

        Path ruta = Paths.get("uploads/casos/" + casoId + "/" + nombreArchivo);
        Resource recurso = new UrlResource(ruta.toUri());

        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

}