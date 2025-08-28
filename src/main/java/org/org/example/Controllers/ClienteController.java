package org.example.Controllers;

import org.example.Models.Caso;
import org.example.Models.Cliente;
import org.example.Request.ClienteRequest;
import org.example.Services.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.getCliente(id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear cliente asociado al abogado autenticado
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente, Authentication auth) {
        try {
            Cliente nuevoCliente = clienteService.crearCliente(cliente, auth.getName());
            return ResponseEntity.ok(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Listar clientes del abogado autenticado
    @GetMapping
    public ResponseEntity<List<Cliente>> listarMisClientes(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Cliente> clientes = clienteService.obtenerClientesPorUsuario(username);
            return ResponseEntity.ok(clientes != null ? clientes : List.of());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(List.of()); // devuelve lista vacía si falla
        }
    }

    // Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody ClienteRequest request) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellido(request.getApellido());
            cliente.setCi(request.getCi());
            cliente.setEmail(request.getEmail());

            Cliente actualizado = clienteService.updateCliente(id, cliente, request.getAbogadosIds());
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.deleteCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener los casos de un cliente
    @GetMapping("/{id}/casos")
    public ResponseEntity<List<Caso>> getCasosByCliente(@PathVariable Long id) {
        try {
            List<Caso> casos = clienteService.getCasosByCliente(id);
            return ResponseEntity.ok(casos != null ? casos : List.of());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(List.of()); // nunca null
        }
    }
}
