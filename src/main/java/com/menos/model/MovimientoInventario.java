package com.menos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_movimiento")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_producto")
    private Producto producto;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="id_pedido")
    private Pedido pedido;           // opcional (si viene de una venta)

    @ManyToOne
    @JoinColumn(name="id_proveedor")
    private Proveedor proveedor;   // opcional (si es una entrada)

    private String tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    // Constructor vacío
    public MovimientoInventario() {
    }

    // --- GETTERS Y SETTERS MANUALES ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}