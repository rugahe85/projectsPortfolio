package com.example.abckafka.service;

import com.example.abckafka.dto.ProductoEvent;
import com.example.abckafka.dto.ProductoEvent.TipoEvento;
import com.example.abckafka.dto.ProductoRequest;
import com.example.abckafka.dto.ProductoResponse;
import com.example.abckafka.entity.Producto;
import com.example.abckafka.exception.DuplicateSkuException;
import com.example.abckafka.exception.ProductoNotFoundException;
import com.example.abckafka.producer.ProductoEventProducer;
import com.example.abckafka.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoEventProducer eventProducer;

    // ── ALTA (Create) ──
    public ProductoResponse crear(ProductoRequest request) {
        log.info("Creando producto con SKU: {}", request.getSku());

        if (repository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Producto producto = Producto.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .sku(request.getSku())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoria(request.getCategoria())
                .activo(true)
                .build();

        Producto saved = repository.save(producto);
        log.info("Producto creado: ID={}, SKU={}", saved.getId(), saved.getSku());

        // Publicar evento CREADO a Kafka
        ProductoEvent evento = ProductoEvent.crear(
                TipoEvento.CREADO, saved.getId(), saved.getSku(), saved.getNombre(),
                saved.getCategoria(), saved.getPrecio(), saved.getStock(), true,
                "Alta de producto: " + saved.getNombre());
        eventProducer.enviarEvento(evento);
        eventProducer.enviarAudit(evento);

        return ProductoResponse.fromEntity(saved);
    }

    // ── Consulta por ID (Read) ──
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return repository.findById(id)
                .map(ProductoResponse::fromEntity)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    // ── Consulta por SKU ──
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorSku(String sku) {
        return repository.findBySku(sku)
                .map(ProductoResponse::fromEntity)
                .orElseThrow(() -> new ProductoNotFoundException(sku));
    }

    // ── Listar activos con paginación ──
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarActivos(Pageable pageable) {
        return repository.findByActivoTrue(pageable).map(ProductoResponse::fromEntity);
    }

    // ── Listar todos con paginación ──
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ProductoResponse::fromEntity);
    }

    // ── Búsqueda por texto ──
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscar(String query, Pageable pageable) {
        return repository.buscar(query, pageable).map(ProductoResponse::fromEntity);
    }

    // ── CAMBIO (Update) ──
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        // Verificar SKU único si cambió
        if (!producto.getSku().equals(request.getSku()) && repository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setSku(request.getSku());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(request.getCategoria());

        Producto updated = repository.save(producto);
        log.info("Producto actualizado: ID={}, SKU={}", updated.getId(), updated.getSku());

        // Publicar evento ACTUALIZADO a Kafka
        ProductoEvent evento = ProductoEvent.crear(
                TipoEvento.ACTUALIZADO, updated.getId(), updated.getSku(), updated.getNombre(),
                updated.getCategoria(), updated.getPrecio(), updated.getStock(), updated.getActivo(),
                "Cambio de producto: " + updated.getNombre());
        eventProducer.enviarEvento(evento);
        eventProducer.enviarAudit(evento);

        return ProductoResponse.fromEntity(updated);
    }

    // ── Actualizar stock ──
    public ProductoResponse actualizarStock(Long id, Integer nuevoStock) {
        log.info("Actualizando stock del producto ID: {} → {}", id, nuevoStock);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        Integer stockAnterior = producto.getStock();
        producto.setStock(nuevoStock);
        Producto updated = repository.save(producto);

        ProductoEvent evento = ProductoEvent.crear(
                TipoEvento.STOCK_ACTUALIZADO, updated.getId(), updated.getSku(), updated.getNombre(),
                updated.getCategoria(), updated.getPrecio(), updated.getStock(), updated.getActivo(),
                String.format("Stock actualizado: %d → %d", stockAnterior, nuevoStock));
        eventProducer.enviarEvento(evento);
        eventProducer.enviarAudit(evento);

        return ProductoResponse.fromEntity(updated);
    }

    // ── BAJA (Soft Delete) ──
    public ProductoResponse eliminar(Long id) {
        log.info("Desactivando producto ID: {}", id);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        producto.setActivo(false);
        Producto deleted = repository.save(producto);
        log.info("Producto desactivado: ID={}, SKU={}", deleted.getId(), deleted.getSku());

        ProductoEvent evento = ProductoEvent.crear(
                TipoEvento.ELIMINADO, deleted.getId(), deleted.getSku(), deleted.getNombre(),
                deleted.getCategoria(), deleted.getPrecio(), deleted.getStock(), false,
                "Baja de producto: " + deleted.getNombre());
        eventProducer.enviarEvento(evento);
        eventProducer.enviarAudit(evento);

        return ProductoResponse.fromEntity(deleted);
    }

    // ── Reactivar producto ──
    public ProductoResponse reactivar(Long id) {
        log.info("Reactivando producto ID: {}", id);

        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        producto.setActivo(true);
        Producto reactivated = repository.save(producto);

        ProductoEvent evento = ProductoEvent.crear(
                TipoEvento.REACTIVADO, reactivated.getId(), reactivated.getSku(), reactivated.getNombre(),
                reactivated.getCategoria(), reactivated.getPrecio(), reactivated.getStock(), true,
                "Reactivación de producto: " + reactivated.getNombre());
        eventProducer.enviarEvento(evento);
        eventProducer.enviarAudit(evento);

        return ProductoResponse.fromEntity(reactivated);
    }

    // ── Categorías disponibles ──
    @Transactional(readOnly = true)
    public List<String> obtenerCategorias() {
        return repository.findCategorias();
    }

    // ── Estadísticas ──
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        return Map.of(
                "totalProductos", repository.count(),
                "productosActivos", repository.countActivos(),
                "productosInactivos", repository.countInactivos(),
                "categorias", repository.findCategorias().size()
        );
    }
}
