package com.menos.service;
import com.menos.model.Producto; import com.menos.repo.ProductoRepository; import org.springframework.stereotype.Service; import java.util.List;
@Service
public class ProductoService {
  private final ProductoRepository repo; public ProductoService(ProductoRepository r){ repo=r; }
  public List<Producto> listar(){ return repo.findAll(); }
  public List<Producto> buscar(String q){ return (q==null || q.isBlank()) ? repo.findAll() : repo.findByNombreContainingIgnoreCase(q.trim()); }
  public Producto obtener(Long id){ return repo.findById(id).orElseThrow(); }
  public Producto guardar(Producto p){ return repo.save(p); }
  public void eliminar(Long id){ repo.deleteById(id); repo.flush(); }
}
