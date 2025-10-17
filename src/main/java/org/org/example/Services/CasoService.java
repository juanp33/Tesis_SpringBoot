package org.example.Services;

import org.example.Models.ArchivoCaso;
import org.example.Models.Caso;
import org.example.Models.Cliente;
import org.example.Repositorios.ArchivoCasoRepository;
import org.example.Repositorios.CasoRepository;
import org.example.Repositorios.ClienteRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

@Service
public class CasoService {

    private final CasoRepository casoRepositorio;
    private final ArchivoCasoRepository archivoCasoRepositorio;
    private final ClienteRepository clienteRepository;

    public CasoService(CasoRepository casoRepositorio, ArchivoCasoRepository archivoCasoRepositorio, ClienteRepository clienteRepository) {
        this.casoRepositorio = casoRepositorio;
        this.archivoCasoRepositorio = archivoCasoRepositorio;
        this.clienteRepository = clienteRepository;
    }


    public Caso crearCaso(Caso caso) {
        return casoRepositorio.save(caso);
    }

    public List<Caso> listarCasosPorCliente(Long clienteId) {
        return casoRepositorio.findByClienteId(clienteId);
    }


    public Caso obtenerCaso(Long id) {
        return casoRepositorio.findById(id).orElseThrow();
    }


    public Caso obtenerCasoSeguro(Long id, String abogadoUsername) {
        Caso caso = obtenerCaso(id);
        if (!caso.getAbogado().equals(abogadoUsername)) {
            throw new RuntimeException("No tienes acceso a este caso");
        }
        return caso;
    }

    public Caso actualizarCasoSeguro(Long id, Caso actualizado, String abogadoUsername) {
        Caso caso = obtenerCasoSeguro(id, abogadoUsername);
        caso.setTitulo(actualizado.getTitulo());
        caso.setTipo(actualizado.getTipo());
        caso.setDescripcion(actualizado.getDescripcion());
        caso.setEstado(actualizado.getEstado());
        return casoRepositorio.save(caso);
    }

    public Caso cambiarEstadoSeguro(Long id, String nuevoEstado, String abogadoUsername) {
        Caso caso = obtenerCasoSeguro(id, abogadoUsername);
        caso.setEstado(nuevoEstado);
        return casoRepositorio.save(caso);
    }

    public ArchivoCaso subirArchivoSeguro(Long casoId, MultipartFile archivo, String abogadoUsername) throws IOException {
        Caso caso = obtenerCasoSeguro(casoId, abogadoUsername);

        String rutaSubida = "uploads/casos/" + casoId;
        Files.createDirectories(Paths.get(rutaSubida));

        Path rutaArchivo = Paths.get(rutaSubida).resolve(archivo.getOriginalFilename());
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        ArchivoCaso archivoCaso = new ArchivoCaso();
        archivoCaso.setNombreArchivo(archivo.getOriginalFilename());
        archivoCaso.setRutaArchivo(rutaArchivo.toString());
        archivoCaso.setCaso(caso);

        return archivoCasoRepositorio.save(archivoCaso);
    }

    public Caso eliminarArchivoSeguro(Long casoId, Long archivoId, String abogadoUsername) {
        Caso caso = obtenerCasoSeguro(casoId, abogadoUsername);
        ArchivoCaso archivo = archivoCasoRepositorio.findById(archivoId).orElseThrow();

        if (!archivo.getCaso().getId().equals(casoId)) {
            throw new RuntimeException("Archivo no pertenece al caso");
        }

        archivoCasoRepositorio.delete(archivo);
        return casoRepositorio.findById(casoId).orElseThrow();
    }

    public void eliminarCasoSeguro(Long id, String abogadoUsername) {
        Caso caso = obtenerCasoSeguro(id, abogadoUsername);
        casoRepositorio.delete(caso);
    }


    public ArchivoCaso subirArchivo(Long casoId, MultipartFile archivo) throws IOException {
        Caso caso = casoRepositorio.findById(casoId).orElseThrow();

        String rutaSubida = "uploads/casos/" + casoId;
        Files.createDirectories(Paths.get(rutaSubida));

        Path rutaArchivo = Paths.get(rutaSubida).resolve(archivo.getOriginalFilename());
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        ArchivoCaso archivoCaso = new ArchivoCaso();
        archivoCaso.setNombreArchivo(archivo.getOriginalFilename());
        archivoCaso.setRutaArchivo(rutaArchivo.toString());
        archivoCaso.setCaso(caso);

        return archivoCasoRepositorio.save(archivoCaso);
    }

    public Resource descargarArchivo(Long casoId, String nombreArchivo) throws IOException {
        Path rutaArchivo = Paths.get("uploads/casos/" + casoId).resolve(nombreArchivo);
        Resource recurso = new UrlResource(rutaArchivo.toUri());

        if (!recurso.exists() || !recurso.isReadable()) {
            throw new IOException("No se puede leer el archivo: " + nombreArchivo);
        }
        return recurso;
    }

    public List<ArchivoCaso> listarArchivos(Long casoId) {
        return archivoCasoRepositorio.findByCasoId(casoId);
    }

    public Caso crearCasoParaCliente(Caso caso, Long clienteId, List<MultipartFile> archivos) throws Exception {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow();
        caso.setCliente(cliente);

        Caso nuevoCaso = casoRepositorio.save(caso);

        if (archivos != null) {
            for (MultipartFile archivo : archivos) {
                subirArchivo(nuevoCaso.getId(), archivo);
            }
        }

        return nuevoCaso;
    }
}

