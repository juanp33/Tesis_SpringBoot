package org.org.example.Response;

public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private Long abogadoId;
    private String abogadoNombre;
    private String abogadoApellido;

    public UsuarioDTO(Long id, String username, String email, Long abogadoId,
                      String abogadoNombre, String abogadoApellido) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.abogadoId = abogadoId;
        this.abogadoNombre = abogadoNombre;
        this.abogadoApellido = abogadoApellido;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getAbogadoId() { return abogadoId; }
    public String getAbogadoNombre() { return abogadoNombre; }
    public String getAbogadoApellido() { return abogadoApellido; }

    // Setters (opcionales, pero por buenas prácticas)
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setAbogadoId(Long abogadoId) { this.abogadoId = abogadoId; }
    public void setAbogadoNombre(String abogadoNombre) { this.abogadoNombre = abogadoNombre; }
    public void setAbogadoApellido(String abogadoApellido) { this.abogadoApellido = abogadoApellido; }
}
