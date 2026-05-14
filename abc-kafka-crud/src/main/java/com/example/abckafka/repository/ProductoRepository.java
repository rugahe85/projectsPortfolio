package com.example.abckafka.repository;

import com.example.abckafka.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Producto> findByActivoTrue(Pageable pageable);

    Page<Producto> findByCategoria(String categoria, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.categoria) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Producto> buscar(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true ORDER BY p.categoria")
    List<String> findCategorias();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long countActivos();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = false")
    long countInactivos();
}
