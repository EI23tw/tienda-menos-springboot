package com.menos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_pedido")
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="id_direccion")
    private DireccionEnvio direccion;          // opcional

    @ManyToOne(optional=false)
    @JoinColumn(name="id_metodo_pago")
    private MetodoPago metodoPago;

    @ManyToOne
    @JoinColumn(name="id_cupon")
    private CuponDescuento cupon;                  // opcional

    private LocalDateTime fecha;
    private BigDecimal total;
    private String estado;

    @Column(name="referencia_pago")
    private String referenciaPago;

    @Column(name="tarjeta_ultimos4")
    private String tarjetaUltimos4;

    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private List<PedidoItem> items = new ArrayList<>();

    // Constructor vacío
    public Pedido() {
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

    public DireccionEnvio getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionEnvio direccion) {
        this.direccion = direccion;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public CuponDescuento getCupon() {
        return cupon;
    }

    public void setCupon(CuponDescuento cupon) {
        this.cupon = cupon;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public String getTarjetaUltimos4() {
        return tarjetaUltimos4;
    }

    public void setTarjetaUltimos4(String tarjetaUltimos4) {
        this.tarjetaUltimos4 = tarjetaUltimos4;
    }

    public List<PedidoItem> getItems() {
        return items;
    }

    public void setItems(List<PedidoItem> items) {
        this.items = items;
    }
}
