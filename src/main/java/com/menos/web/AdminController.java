package com.menos.web;

import com.menos.model.*;
import com.menos.repo.*;
import com.menos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PedidoRepository ped;
    private final ProductoService ps;
    private final CategoriaRepository cr;

    public AdminController(PedidoRepository ped, ProductoService ps, CategoriaRepository cr) {
        this.ped = ped;
        this.ps = ps;
        this.cr = cr;
    }

    @GetMapping
    public String pedidos(Model m) {
        List<Pedido> l = ped.findAllByOrderByFechaDesc();
        m.addAttribute("pedidos", l);

        BigDecimal ventas = BigDecimal.ZERO;
        // Bucle clásico en lugar de filter().map().reduce()
        for (Pedido p : l) {
            if (!p.getEstado().equals("RECHAZADO")) {
                ventas = ventas.add(p.getTotal());
            }
        }
        m.addAttribute("ventas", ventas);
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/{id}/estado")
    public String estado(@PathVariable Long id, @RequestParam String estado) {
        Pedido x = ped.findById(id).orElseThrow();
        x.setEstado(estado);
        ped.save(x);
        return "redirect:/admin";
    }

    @GetMapping("/productos")
    public String productos(@RequestParam(required = false) String q, Model m) {
        m.addAttribute("productos", ps.buscar(q));
        m.addAttribute("q", q);
        return "admin/productos";
    }

    @PostMapping("/productos")
    public String nuevo(@RequestParam String nombre, @RequestParam String descripcion, @RequestParam BigDecimal precio, @RequestParam int stock, @RequestParam String categoria, @RequestParam String emoji) {
        return guardar(new Producto(), nombre, descripcion, precio, stock, categoria, emoji);
    }

    @GetMapping("/productos/{id}/editar")
    public String editar(@PathVariable Long id, Model m) {
        m.addAttribute("p", ps.obtener(id));
        return "admin/producto-form";
    }

    @PostMapping("/productos/{id}/editar")
    public String actualizar(@PathVariable Long id, @RequestParam(required = false) String imagen, @RequestParam String nombre, @RequestParam String descripcion, @RequestParam BigDecimal precio, @RequestParam int stock, @RequestParam String categoria, @RequestParam String emoji) {
        Producto p = ps.obtener(id);
        if (imagen != null) {
            if (imagen.isBlank()) {
                p.setImagen(null);
            } else {
                p.setImagen(imagen);
            }
        }
        return guardar(p, nombre, descripcion, precio, stock, categoria, emoji);
    }

    private String guardar(Producto p, String n, String d, BigDecimal pr, int st, String c, String e) {
        p.setNombre(n);
        p.setDescripcion(d);
        p.setPrecio(pr);
        p.setStock(st);
        p.setCategoria(categoria(c));
        p.setEmoji(e);
        ps.guardar(p);
        return "redirect:/admin/productos";
    }

    /** Busca la categoría por nombre; si no existe, la crea al estilo tradicional */
    private Categoria categoria(String nombre) {
        String n = nombre.trim();
        Optional<Categoria> opt = cr.findByNombre(n);
        
        if (opt.isPresent()) {
            return opt.get();
        } else {
            Categoria c = new Categoria();
            c.setNombre(n);
            return cr.save(c);
        }
    }

    @PostMapping("/productos/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ps.eliminar(id);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se puede eliminar: el producto ya figura en compras. Pon su stock en 0.");
        }
        return "redirect:/admin/productos";
    }
}