package com.menos.config;

import com.menos.model.*;
import com.menos.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository ur;
    private final ProductoRepository pr;
    private final PasswordEncoder enc;
    private final CategoriaRepository cr;
    private final MetodoPagoRepository mpr;

    public DataLoader(UsuarioRepository ur, ProductoRepository pr, PasswordEncoder enc, CategoriaRepository cr, MetodoPagoRepository mpr) {
        this.ur = ur;
        this.pr = pr;
        this.enc = enc;
        this.cr = cr;
        this.mpr = mpr;
    }

    public void run(String... args) {
        // 1. Crear Método de Pago
        if (mpr.findByNombre("Tarjeta").isEmpty()) {
            MetodoPago m = new MetodoPago();
            m.setNombre("Tarjeta");
            m.setActivo(true);
            mpr.save(m);
        }

        // 2. Crear Usuarios
        if (ur.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@menos.pe");
            admin.setPassword(enc.encode("admin123"));
            admin.setRol("ADMIN");
            ur.save(admin);

            Usuario cliente = new Usuario();
            cliente.setNombre("Cliente Demo");
            cliente.setEmail("cliente@menos.pe");
            cliente.setPassword(enc.encode("cliente123"));
            cliente.setRol("CLIENTE");
            ur.save(cliente);
        }

        // 3. Crear Productos al estilo tradicional
        if (pr.count() == 0) {
            Categoria catAbarrotes = obtenerOCrearCategoria("Abarrotes");
            Categoria catLacteos = obtenerOCrearCategoria("Lácteos");
            Categoria catFrutas = obtenerOCrearCategoria("Frutas y verduras");
            Categoria catBebidas = obtenerOCrearCategoria("Bebidas");
            Categoria catLimpieza = obtenerOCrearCategoria("Limpieza");

            Producto p1 = new Producto();
            p1.setNombre("Arroz costeño 5 kg");
            p1.setDescripcion("Grano seleccionado");
            p1.setPrecio(new BigDecimal("21.90"));
            p1.setStock(50);
            p1.setCategoria(catAbarrotes);
            p1.setEmoji("🍚");
            pr.save(p1);

            Producto p2 = new Producto();
            p2.setNombre("Aceite vegetal 1 L");
            p2.setDescripcion("Botella pet");
            p2.setPrecio(new BigDecimal("8.50"));
            p2.setStock(40);
            p2.setCategoria(catAbarrotes);
            p2.setEmoji("🛢️");
            pr.save(p2);

            Producto p3 = new Producto();
            p3.setNombre("Fideos spaghetti 500 g");
            p3.setDescripcion("Pasta de sémola");
            p3.setPrecio(new BigDecimal("3.20"));
            p3.setStock(80);
            p3.setCategoria(catAbarrotes);
            p3.setEmoji("🍝");
            pr.save(p3);

            Producto p4 = new Producto();
            p4.setNombre("Azúcar rubia 1 kg");
            p4.setDescripcion("Bolsa");
            p4.setPrecio(new BigDecimal("4.30"));
            p4.setStock(60);
            p4.setCategoria(catAbarrotes);
            p4.setEmoji("🧂");
            pr.save(p4);

            Producto p5 = new Producto();
            p5.setNombre("Leche entera 1 L");
            p5.setDescripcion("Caja tetra");
            p5.setPrecio(new BigDecimal("4.90"));
            p5.setStock(70);
            p5.setCategoria(catLacteos);
            p5.setEmoji("🥛");
            pr.save(p5);

            Producto p6 = new Producto();
            p6.setNombre("Queso fresco 250 g");
            p6.setDescripcion("Artesanal");
            p6.setPrecio(new BigDecimal("9.90"));
            p6.setStock(25);
            p6.setCategoria(catLacteos);
            p6.setEmoji("🧀");
            pr.save(p6);

            Producto p7 = new Producto();
            p7.setNombre("Yogurt fresa 1 L");
            p7.setDescripcion("Botella");
            p7.setPrecio(new BigDecimal("7.20"));
            p7.setStock(35);
            p7.setCategoria(catLacteos);
            p7.setEmoji("🍓");
            pr.save(p7);

            Producto p8 = new Producto();
            p8.setNombre("Plátano de seda 1 kg");
            p8.setDescripcion("De la selva");
            p8.setPrecio(new BigDecimal("3.90"));
            p8.setStock(45);
            p8.setCategoria(catFrutas);
            p8.setEmoji("🍌");
            pr.save(p8);
            
            Producto p9 = new Producto();
            p9.setNombre("Palta fuerte 1 kg");
            p9.setDescripcion("Calidad extra");
            p9.setPrecio(new BigDecimal("12.90"));
            p9.setStock(30);
            p9.setCategoria(catFrutas);
            p9.setEmoji("🥑");
            pr.save(p9);

            Producto p10 = new Producto();
            p10.setNombre("Tomate 1 kg");
            p10.setDescripcion("Fresco");
            p10.setPrecio(new BigDecimal("4.50"));
            p10.setStock(40);
            p10.setCategoria(catFrutas);
            p10.setEmoji("🍅");
            pr.save(p10);

            Producto p11 = new Producto();
            p11.setNombre("Gaseosa 3 L");
            p11.setDescripcion("Sabor cola");
            p11.setPrecio(new BigDecimal("10.50"));
            p11.setStock(50);
            p11.setCategoria(catBebidas);
            p11.setEmoji("🥤");
            pr.save(p11);

            Producto p12 = new Producto();
            p12.setNombre("Agua sin gas 2.5 L");
            p12.setDescripcion("Botella");
            p12.setPrecio(new BigDecimal("3.50"));
            p12.setStock(90);
            p12.setCategoria(catBebidas);
            p12.setEmoji("💧");
            pr.save(p12);

            Producto p13 = new Producto();
            p13.setNombre("Detergente 2 kg");
            p13.setDescripcion("Ropa blanca y color");
            p13.setPrecio(new BigDecimal("19.90"));
            p13.setStock(30);
            p13.setCategoria(catLimpieza);
            p13.setEmoji("🧴");
            pr.save(p13);

            Producto p14 = new Producto();
            p14.setNombre("Lejía 1 L");
            p14.setDescripcion("Desinfectante");
            p14.setPrecio(new BigDecimal("4.20"));
            p14.setStock(55);
            p14.setCategoria(catLimpieza);
            p14.setEmoji("🧽");
            pr.save(p14);
        }
    }

    private Categoria obtenerOCrearCategoria(String nombre) {
        Optional<Categoria> opt = cr.findByNombre(nombre);
        if (opt.isPresent()) {
            return opt.get();
        } else {
            Categoria c = new Categoria();
            c.setNombre(nombre);
            return cr.save(c);
        }
    }
}