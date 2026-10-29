package com.menos.web;

import com.menos.model.*;
import com.menos.repo.*;
import com.menos.service.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TiendaController {

    private final ProductoRepository pr;
    private final UsuarioRepository ur;
    private final PedidoRepository ped;
    private final Carrito carrito;
    private final CarritoService cs;
    private final PasswordEncoder enc;

    public TiendaController(ProductoRepository pr, UsuarioRepository ur, PedidoRepository ped, Carrito c, CarritoService cs, PasswordEncoder enc) {
        this.pr = pr;
        this.ur = ur;
        this.ped = ped;
        this.carrito = c;
        this.cs = cs;
        this.enc = enc;
    }

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String cat, Model m) {
        List<Producto> productosDB;
        if (cat == null) {
            productosDB = pr.findAll();
        } else {
            productosDB = pr.findByCategoriaNombre(cat);
        }

        //Mapeo manual de Entidad a DTO usando múltiples tablas
        List<com.menos.dto.ProductoDTO> productosDTO = new ArrayList<>();
        for (Producto p : productosDB) {
            com.menos.dto.ProductoDTO dto = new com.menos.dto.ProductoDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setDescripcion(p.getDescripcion()); // <- Agregado
            dto.setPrecio(p.getPrecio());
            dto.setCategoria(p.getCategoria().getNombre()); 
            dto.setStock(p.getStock());             // <- Agregado
            dto.setImagen(p.getImagen());
            dto.setEmoji(p.getEmoji());
            productosDTO.add(dto);
        }

        m.addAttribute("productos", productosDTO);
        m.addAttribute("cat", cat);

        // Lógica tradicional de categorías
        List<String> categorias = new ArrayList<>();
        List<Producto> todosLosProductos = pr.findAll();
        for (Producto p : todosLosProductos) {
            String nombreCat = p.getCategoria().getNombre();
            if (!categorias.contains(nombreCat)) {
                categorias.add(nombreCat);
            }
        }
        m.addAttribute("categorias", categorias);

        return "index";
    }

    @GetMapping("/cesta")
    public String cesta(Model m) {
        m.addAttribute("lineas", cs.lineas());
        m.addAttribute("total", cs.total());
        return "cesta";
    }

    @PostMapping("/cesta/cambiar/{id}")
    public String cambiar(@PathVariable Long id, @RequestParam int delta, @RequestHeader(value = "Referer", required = false) String ref) {
        carrito.cambiar(id, delta);
        if (ref == null) {
            return "redirect:/cesta";
        } else {
            return "redirect:" + ref;
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, Model m) {
        Optional<Usuario> opt = ur.findByEmail(email);
        
        if (opt.isPresent()) {
            m.addAttribute("error", "Ese correo ya tiene una cuenta.");
            return "registro";
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(enc.encode(password));
        u.setRol("CLIENTE");
        ur.save(u);

        return "redirect:/login?registrado";
    }

    @GetMapping("/mis-compras")
    public String mis(Principal p, Model m) {
        Optional<Usuario> optUsuario = ur.findByEmail(p.getName());
        
        if (optUsuario.isPresent()) {
            Usuario u = optUsuario.get();
            List<Pedido> pedidos = ped.findByUsuarioOrderByFechaDesc(u);
            m.addAttribute("pedidos", pedidos);
        } else {
            m.addAttribute("pedidos", new ArrayList<>());
        }
        
        return "mis-compras";
    }
}