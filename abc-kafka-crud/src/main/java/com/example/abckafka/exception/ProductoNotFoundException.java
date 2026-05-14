package com.example.abckafka.exception;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(Long id) {
        super("Producto no encontrado con ID: " + id);
    }

    public ProductoNotFoundException(String sku) {
        super("Producto no encontrado con SKU: " + sku);
    }
}
