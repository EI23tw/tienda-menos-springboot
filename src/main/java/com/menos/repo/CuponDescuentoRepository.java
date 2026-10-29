package com.menos.repo;

import com.menos.model.CuponDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuponDescuentoRepository extends JpaRepository<CuponDescuento, Long> {
    // Busca el cupón por su código (ej. "MENOS10")
    Optional<CuponDescuento> findByCodigo(String codigo);
}