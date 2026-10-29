package com.menos.service;

import com.menos.model.Producto;
import com.menos.repo.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarritoService {

    // Clase tradicional en lugar de un "record"
    public static class Linea {
        private Producto producto;
        private int cantidad;
        private BigDecimal subtotal;

        public Linea(Producto producto, int cantidad, BigDecimal subtotal) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.subtotal = subtotal;
        }

        public Producto getProducto() { return producto; }
        public void setProducto(Producto producto) { this.producto = producto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    private final Carrito carrito;
    private final ProductoRepository repo;

    public CarritoService(Carrito c, ProductoRepository r) {
        this.carrito = c;
        this.repo = r;
    }

    public List<Linea> lineas() {
        List<Linea> out = new ArrayList<>();
        
        // Bucle for tradicional en lugar de forEach y lambdas
        for (Map.Entry<Long, Integer> entry : carrito.getItems().entrySet()) {
            Long idProducto = entry.getKey();
            Integer cantidad = entry.getValue();
            
            Optional<Producto> opt = repo.findById(idProducto);
            if (opt.isPresent()) {
                Producto p = opt.get();
                BigDecimal subtotal = p.getPrecio().multiply(BigDecimal.valueOf(cantidad));
                Linea linea = new Linea(p, cantidad, subtotal);
                out.add(linea);
            }
        }
        return out;
    }

    public BigDecimal total() {
        BigDecimal sumaTotal = BigDecimal.ZERO;
        List<Linea> lista = lineas();
        
        // Bucle for tradicional en lugar de stream().reduce()
        for (Linea l : lista) {
            sumaTotal = sumaTotal.add(l.getSubtotal());
        }
        return sumaTotal;
    }
}