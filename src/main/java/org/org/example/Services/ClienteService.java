package org.example.Services;

import org.example.Models.Abogado;
import org.example.Models.Caso;
import org.example.Models.Cliente;
import org.example.Models.Usuario;
import org.example.Repositorios.AbogadoRepository;
import org.example.Repositorios.ClienteRepository;
import org.example.Repositorios.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AbogadoRepository abogadoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          AbogadoRepository abogadoRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.abogadoRepository = abogadoRepository;
    }

    // 🔹 Listar clientes del abogado autenticado
    public List<Cliente> obtenerClientesPorUsuario(String usernameUsuario) {
        Usuario usuario = usuarioRepository.findByUsername(usernameUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Abogado abogado = usuario.getAbogado();
        if (abogado == null) {
            throw new RuntimeException("El usuario no tiene un abogado asociado");
        }

        return clienteRepository.findByAbogadosContains(abogado);
    }

    // 🔹 Crear cliente y vincularlo al abogado autenticado
    public Cliente crearCliente(Cliente cliente, String usernameUsuario) {
        Usuario usuario = usuarioRepository.findByUsername(usernameUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Abogado abogado = usuario.getAbogado();
        if (abogado == null) {
            throw new RuntimeException("El usuario no tiene un abogado asociado");
        }

        if (cliente.getAbogados() == null) {
            cliente.setAbogados(new ArrayList<>());
        }
        cliente.getAbogados().add(abogado);

        return clienteRepository.save(cliente);
    }

    // 🔹 Obtener todos los clientes (global)
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    // 🔹 Vincular cliente existente al abogado autenticado
    @Transactional
    public Cliente vincularClienteConAbogado(Long clienteId, String usernameUsuario) {
        Usuario usuario = usuarioRepository.findByUsername(usernameUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Abogado abogado = usuario.getAbogado();
        if (abogado == null) {
            throw new RuntimeException("El usuario no tiene un abogado asociado");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (!cliente.getAbogados().contains(abogado)) {
            cliente.getAbogados().add(abogado);
            cliente = clienteRepository.save(cliente);
        }

        return cliente;
    }

    // 🔹 Obtener cliente por ID
    public Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // 🔹 Crear cliente asignando abogados por IDs
    @Transactional
    public Cliente createCliente(Cliente cliente, List<Long> abogadosIds) {
        if (abogadosIds != null && !abogadosIds.isEmpty()) {
            List<Abogado> abogados = abogadoRepository.findAllById(abogadosIds);
            cliente.setAbogados(abogados);
        }
        return clienteRepository.save(cliente);
    }

    // 🔹 Actualizar cliente
    @Transactional
    public Cliente updateCliente(Long id, Cliente updatedCliente, List<Long> abogadosIds) {
        Cliente cliente = getCliente(id);

        cliente.setNombre(updatedCliente.getNombre());
        cliente.setApellido(updatedCliente.getApellido());
        cliente.setCi(updatedCliente.getCi());
        cliente.setEmail(updatedCliente.getEmail());

        if (abogadosIds != null) {
            List<Abogado> abogados = abogadoRepository.findAllById(abogadosIds);
            cliente.setAbogados(abogados);
        }

        return clienteRepository.save(cliente);
    }

    // 🔹 Eliminar cliente
    public void deleteCliente(Long id) {
        Cliente cliente = getCliente(id); // valida existencia
        clienteRepository.delete(cliente);
    }

    // 🔹 Obtener casos de un cliente
    public List<Caso> getCasosByCliente(Long clienteId) {
        Cliente cliente = getCliente(clienteId);
        return cliente.getCasos() != null ? cliente.getCasos() : new ArrayList<>();
    }
}
