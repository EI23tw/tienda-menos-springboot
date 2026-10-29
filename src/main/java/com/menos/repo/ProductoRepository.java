package com.menos.repo;
import com.menos.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto,Long>{ List<Producto> findByCategoriaNombre(String c); List<Producto> findByNombreContainingIgnoreCase(String q); }
