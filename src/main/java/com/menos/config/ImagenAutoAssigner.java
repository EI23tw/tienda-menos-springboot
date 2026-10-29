package com.menos.config;

import com.menos.model.Producto;
import com.menos.repo.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Al iniciar la aplicacion, busca en static/img/productos/ archivos de imagen
 * cuyo nombre coincida con el nombre de algun producto, y le asigna esa ruta
 * automaticamente (sin tener que hacerlo a mano desde /admin/productos).
 *
 * Ejemplo: si existe el archivo "arroz.jpg" y hay un producto llamado
 * "Arroz costeño 5 kg", se le asigna "/img/productos/arroz.jpg".
 *
 * Si un producto YA tiene una imagen asignada (por ejemplo, puesta a mano
 * desde el panel de admin), este proceso no la toca ni la sobrescribe.
 * Se ejecuta en cada arranque, asi que basta con copiar un archivo nuevo a
 * la carpeta y reiniciar la aplicacion para que se enlace solo.
 */
@Component
@Order(2)
public class ImagenAutoAssigner implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public ImagenAutoAssigner(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] archivos;
        try {
            archivos = resolver.getResources("classpath:/static/img/productos/*.*");
        } catch (Exception e) {
            // La carpeta no existe todavia o no se pudo leer: no hay nada que asignar.
            return;
        }

        List<Producto> productos = productoRepository.findAll();

        for (Resource archivo : archivos) {
            String nombreArchivo = archivo.getFilename();
            if (nombreArchivo == null || !esImagen(nombreArchivo)) {
                continue;
            }

            String clave = normalizar(quitarExtension(nombreArchivo));
            if (clave.isBlank()) {
                continue;
            }

            for (Producto producto : productos) {
                if (producto.getImagen() != null && !producto.getImagen().isBlank()) {
                    continue; // ya tiene imagen asignada, no se toca
                }
                if (normalizar(producto.getNombre()).contains(clave)) {
                    producto.setImagen("/img/productos/" + nombreArchivo);
                    productoRepository.save(producto);
                }
            }
        }
    }

    private boolean esImagen(String nombreArchivo) {
        String n = nombreArchivo.toLowerCase();
        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp");
    }

    private String quitarExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        return punto > 0 ? nombreArchivo.substring(0, punto) : nombreArchivo;
    }

    /** Pasa a minusculas, quita tildes y deja solo letras/numeros, para poder comparar sin errores. */
    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return Pattern.compile("[^a-z0-9]").matcher(sinTildes).replaceAll("");
    }
}
