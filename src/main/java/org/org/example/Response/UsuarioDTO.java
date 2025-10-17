package org.org.example.Response;

public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private Long abogadoId;

    public UsuarioDTO(Long id, String username, String email, Long abogadoId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.abogadoId = abogadoId;
    }


    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getAbogadoId() { return abogadoId; }
}