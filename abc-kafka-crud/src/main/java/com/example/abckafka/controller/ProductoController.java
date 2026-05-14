package com.example.abckafka.controller;

import com.example.abckafka.dto.ProductoRequest;
import com.example.abckafka.dto.ProductoResponse;
import com.example.abckafka.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones ABC (Alta-Baja-Cambio) de productos")
public class ProductoController {

    private final ProductoService service;

    // ── ALTA (Create) ──
    @PostMapping
    @Operation(summary = "Alta de producto", description = "Crea un nuevo producto y publica evento a Kafka")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación"),
        @ApiResponse(responseCode = "409", description = "SKU duplicado")
    })
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    // ── Consultar por ID ──
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // ── Consultar por SKU ──
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener producto por SKU")
    public ResponseEntity<ProductoResponse> obtenerPorSku(
            @Parameter(description = "SKU del producto") @PathVariable String sku) {
        return ResponseEntity.ok(service.obtenerPorSku(sku));
    }

    // ── Listar todos (paginado) ──
    @GetMapping
    @Operation(summary = "Listar productos", description = "Lista todos los productos con paginación")
    public ResponseEntity<Page<ProductoResponse>> listar(
            @Parameter(description = "Solo productos activos") @RequestParam(defaultValue = "true") boolean soloActivos,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductoResponse> page = soloActivos
                ? service.listarActivos(pageable)
                : service.listarTodos(pageable);
        return ResponseEntity.ok(page);
    }

    // ── Buscar ──
    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", description = "Búsqueda por nombre, SKU o categoría")
    public ResponseEntity<Page<ProductoResponse>> buscar(
            @Parameter(description = "Texto de búsqueda") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.buscar(q, pageable));
    }

    // ── CAMBIO (Update) ──
    @PutMapping("/{id}")
    @Operation(summary = "Cambio de producto", description = "Actualiza un producto y publica evento a Kafka")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "409", description = "SKU duplicado")
    })
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // ── Actualizar stock ──
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock", description = "Modifica el stock y publica evento a Kafka")
    public ResponseEntity<ProductoResponse> actualizarStock(
            @PathVariable Long id,
            @RequestParam @Min(0) Integer cantidad) {
        return ResponseEntity.ok(service.actualizarStock(id, cantidad));
    }

    // ── BAJA (Soft Delete) ──
    @DeleteMapping("/{id}")
    @Operation(summary = "Baja de producto", description = "Desactiva un producto (soft delete) y publica evento a Kafka")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto desactivado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponse> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminar(id));
    }

    // ── Reactivar ──
    @PatchMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar producto", description = "Reactiva un producto previamente dado de baja")
    public ResponseEntity<ProductoResponse> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reactivar(id));
    }

    // ── Categorías ──
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías disponibles")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(service.obtenerCategorias());
    }

    // ── Estadísticas ──
    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas generales", description = "Total de productos, activos, inactivos y categorías")
    public ResponseEntity<Map<String, Object>> estadisticas() {
        return ResponseEntity.ok(service.obtenerEstadisticas());
    }
}
