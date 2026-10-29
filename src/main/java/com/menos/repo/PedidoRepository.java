package com.menos.repo;
import com.menos.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface PedidoRepository extends JpaRepository<Pedido,Long>{ List<Pedido> findByUsuarioOrderByFechaDesc(Usuario u); List<Pedido> findAllByOrderByFechaDesc(); }
