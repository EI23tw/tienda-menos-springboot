package com.menos.model;

import jakarta.persistence.*;

@Entity
@Table(name="direccion_envio")
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_direccion")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    private String direccion;
    private String distrito;
    private String referencia;

    // Constructor vacío
    public DireccionEnvio() {
    }

    // --- GETTERS Y SETTERS MANUALES ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}