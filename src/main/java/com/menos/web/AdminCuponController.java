package com.menos.web;

import com.menos.model.CuponDescuento;
import com.menos.repo.CuponDescuentoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/cupones")
public class AdminCuponController {

    private final CuponDescuentoRepository cdr;

    public AdminCuponController(CuponDescuentoRepository cdr) {
        this.cdr = cdr;
    }

    @GetMapping
    public String listar(Model m) {
        m.addAttribute("cupones", cdr.findAll());
        return "admin/cupones";
    }

    @PostMapping("/nuevo")
    public String nuevo(@RequestParam String codigo, @RequestParam BigDecimal porcentaje, @RequestParam String fecha) {
        CuponDescuento c = new CuponDescuento();
        c.setCodigo(codigo.toUpperCase().trim());
        c.setPorcentajeDescuento(porcentaje);
        c.setFechaExpiracion(LocalDate.parse(fecha));
        c.setActivo(true);
        cdr.save(c);
        return "redirect:/admin/cupones";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id) {
        Optional<CuponDescuento> opt = cdr.findById(id);
        if (opt.isPresent()) {
            CuponDescuento c = opt.get();
            // Alternar estado de forma tradicional
            if (c.isActivo()) {
                c.setActivo(false);
            } else {
                c.setActivo(true);
            }
            cdr.save(c);
        }
        return "redirect:/admin/cupones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        cdr.deleteById(id);
        return "redirect:/admin/cupones";
    }
}