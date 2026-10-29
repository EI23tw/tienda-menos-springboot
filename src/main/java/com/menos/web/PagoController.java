package com.menos.web;

import com.menos.model.*;
import com.menos.repo.*;
import com.menos.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/pago")
public class PagoController {

    private final CarritoService cs;
    private final Carrito carrito;
    private final PedidoRepository ped;
    private final UsuarioRepository ur;
    private final ProductoRepository pr;
    private final MetodoPagoRepository mpr;
    private final CuponDescuentoRepository cdr;

    public PagoController(CarritoService cs, Carrito c, PedidoRepository ped, UsuarioRepository ur, ProductoRepository pr, MetodoPagoRepository mpr, CuponDescuentoRepository cdr) {
        this.cs = cs;
        this.carrito = c;
        this.ped = ped;
        this.ur = ur;
        this.pr = pr;
        this.mpr = mpr;
        this.cdr = cdr;
    }

    @GetMapping
    public String pago(Model m, HttpSession session) {
        if (carrito.count() == 0) {
            return "redirect:/cesta";
        }
        
        BigDecimal subtotal = cs.total();
        BigDecimal descuento = BigDecimal.ZERO;
        
        // Recuperar cupón de la sesión tradicional
        CuponDescuento cupon = (CuponDescuento) session.getAttribute("cuponAplicado");
        
        if (cupon != null) {
            // Regla de tres simple con BigDecimal
            descuento = subtotal.multiply(cupon.getPorcentajeDescuento()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal totalFinal = subtotal.subtract(descuento);
        
        m.addAttribute("lineas", cs.lineas());
        m.addAttribute("subtotal", subtotal);
        m.addAttribute("descuento", descuento);
        m.addAttribute("totalFinal", totalFinal);
        m.addAttribute("cupon", cupon);
        
        return "pago";
    }

    @PostMapping("/cupon")
    public String aplicarCupon(@RequestParam String codigo, HttpSession session, RedirectAttributes ra) {
        Optional<CuponDescuento> opt = cdr.findByCodigo(codigo.toUpperCase());
        
        if (opt.isPresent()) {
            CuponDescuento c = opt.get();
            if (c.isActivo()) {
                session.setAttribute("cuponAplicado", c);
                ra.addFlashAttribute("mensajeCupon", "Cupón aplicado correctamente.");
            } else {
                ra.addFlashAttribute("errorCupon", "El cupón ya no es válido o está inactivo.");
            }
        } else {
            ra.addFlashAttribute("errorCupon", "El cupón ingresado no existe.");
        }
        return "redirect:/pago";
    }

    @PostMapping("/procesar")
    public String procesar(
            @RequestParam String metodo, 
            @RequestParam(required = false, defaultValue = "") String numero, 
            @RequestParam(required = false, defaultValue = "") String titular, 
            Principal p, 
            HttpSession session,
            RedirectAttributes ra) {
        
        if (carrito.count() == 0) {
            return "redirect:/cesta";
        }

        boolean ok = true;
        Pedido x = new Pedido();
        
        Optional<Usuario> optUsuario = ur.findByEmail(p.getName());
        if (optUsuario.isPresent()) {
            x.setUsuario(optUsuario.get());
        }
        
        x.setFecha(LocalDateTime.now());

        Optional<MetodoPago> optMetodo = mpr.findByNombre(metodo);
        if (optMetodo.isPresent()) {
            x.setMetodoPago(optMetodo.get());
        } else {
            MetodoPago m = new MetodoPago();
            m.setNombre(metodo);
            m.setActivo(true);
            x.setMetodoPago(mpr.save(m));
        }

        if (metodo.equals("Tarjeta")) {
            String d = numero.replaceAll("\\D", "");
            if (d.length() < 13) {
                ra.addFlashAttribute("error", "Revisa el número de tarjeta.");
                return "redirect:/pago";
            }
            ok = !d.endsWith("0000"); 
            x.setTarjetaUltimos4(d.substring(d.length() - 4));
        } else {
            x.setTarjetaUltimos4(null);
            ok = true;
        }

        // Volver a calcular con cupón para evitar alteraciones desde el HTML
        BigDecimal subtotal = cs.total();
        BigDecimal descuento = BigDecimal.ZERO;
        CuponDescuento cupon = (CuponDescuento) session.getAttribute("cuponAplicado");
        
        if (cupon != null) {
            descuento = subtotal.multiply(cupon.getPorcentajeDescuento()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            x.setCupon(cupon);
        }
        
        BigDecimal totalFinal = subtotal.subtract(descuento);
        x.setTotal(totalFinal);
        
        if (ok) {
            x.setEstado("PAGADO");
        } else {
            x.setEstado("RECHAZADO");
        }
        
        x.setReferenciaPago("MP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        for (CarritoService.Linea l : cs.lineas()) {
            PedidoItem i = new PedidoItem();
            i.setPedido(x);
            i.setProducto(l.getProducto());
            i.setCantidad(l.getCantidad());
            i.setPrecioUnitario(l.getProducto().getPrecio());
            x.getItems().add(i);

            if (ok) {
                int nuevoStock = l.getProducto().getStock() - l.getCantidad();
                if (nuevoStock < 0) {
                    nuevoStock = 0;
                }
                l.getProducto().setStock(nuevoStock);
                pr.save(l.getProducto());
            }
        }

        ped.save(x);
        
        if (ok) {
            carrito.clear();
            session.removeAttribute("cuponAplicado"); // Limpiar sesión clásica
        }
        
        return "redirect:/pago/resultado/" + x.getId();
    }

    @GetMapping("/resultado/{id}")
    public String resultado(@PathVariable Long id, Principal p, Model m) {
        Optional<Pedido> optPedido = ped.findById(id);
        
        if (optPedido.isPresent()) {
            Pedido x = optPedido.get();
            if (!x.getUsuario().getEmail().equals(p.getName())) {
                return "redirect:/";
            }
            m.addAttribute("pedido", x);
            return "resultado";
        }
        return "redirect:/";
    }
}