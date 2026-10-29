package com.menos.web;

import com.menos.model.*;
import com.menos.repo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/admin/graficos")
public class GraficosController {

    private final PedidoRepository ped;
    private final ProductoRepository pr;

    public GraficosController(PedidoRepository ped, ProductoRepository pr) {
        this.ped = ped;
        this.pr = pr;
    }

    @GetMapping("/ventas")
    public String ventas(Model m) {
        Map<String, BigDecimal> porDia = new TreeMap<>();
        Map<String, BigDecimal> porProd = new LinkedHashMap<>();

        // Bucle for tradicional para procesar pedidos
        for (Pedido x : ped.findAll()) {
            if (x.getEstado().equals("RECHAZADO")) {
                continue;
            }

            // Acumular ventas por día de forma clásica
            String fechaStr = x.getFecha().toLocalDate().toString();
            BigDecimal totalPedido = x.getTotal();
            
            if (porDia.containsKey(fechaStr)) {
                porDia.put(fechaStr, porDia.get(fechaStr).add(totalPedido));
            } else {
                porDia.put(fechaStr, totalPedido);
            }

            // Bucle for tradicional para procesar items y acumular por producto
            for (PedidoItem i : x.getItems()) {
                String nombreProd = i.getProducto().getNombre();
                BigDecimal subtotalItem = i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()));
                
                if (porProd.containsKey(nombreProd)) {
                    porProd.put(nombreProd, porProd.get(nombreProd).add(subtotalItem));
                } else {
                    porProd.put(nombreProd, subtotalItem);
                }
            }
        }

        m.addAttribute("dias", new ArrayList<>(porDia.keySet()));
        m.addAttribute("ventasDia", new ArrayList<>(porDia.values()));
        m.addAttribute("productos", new ArrayList<>(porProd.keySet()));
        m.addAttribute("ventasProd", new ArrayList<>(porProd.values()));
        
        return "admin/graficos-ventas";
    }

    @GetMapping("/inventario")
    public String inventario(Model m) {
        List<Producto> ps = pr.findAll();

        // 1. Extraer nombres y stocks (Reemplaza a los Streams)
        List<String> nombres = new ArrayList<>();
        List<Integer> stocks = new ArrayList<>();
        for (Producto p : ps) {
            nombres.add(p.getNombre());
            stocks.add(p.getStock());
        }

        // 2. Agrupar estados de los pedidos (Reemplaza a Collectors.groupingBy)
        Map<String, Long> estados = new TreeMap<>();
        for (Pedido pedido : ped.findAll()) {
            String estado = pedido.getEstado();
            if (estados.containsKey(estado)) {
                estados.put(estado, estados.get(estado) + 1L);
            } else {
                estados.put(estado, 1L);
            }
        }

        // 3. Agrupar productos por categoría (Reemplaza a Collectors.groupingBy)
        Map<String, Long> cats = new TreeMap<>();
        for (Producto p : ps) {
            String catNombre = p.getCategoria().getNombre();
            if (cats.containsKey(catNombre)) {
                cats.put(catNombre, cats.get(catNombre) + 1L);
            } else {
                cats.put(catNombre, 1L);
            }
        }

        m.addAttribute("nombres", nombres);
        m.addAttribute("stocks", stocks);
        m.addAttribute("estados", new ArrayList<>(estados.keySet()));
        m.addAttribute("cantEstados", new ArrayList<>(estados.values()));
        m.addAttribute("cats", new ArrayList<>(cats.keySet()));
        m.addAttribute("cantCats", new ArrayList<>(cats.values()));
        
        return "admin/graficos-inventario";
    }
}