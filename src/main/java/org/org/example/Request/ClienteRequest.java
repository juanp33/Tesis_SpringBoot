package org.example.Request;

import java.util.List;

public class ClienteRequest {
    private String nombre;
    private String apellido;
    private String ci;
    private String email;
    private List<Long> abogadosIds;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getAbogadosIds() {
        return abogadosIds;
    }

    public void setAbogadosIds(List<Long> abogadosIds) {
        this.abogadosIds = abogadosIds;
    }
}
