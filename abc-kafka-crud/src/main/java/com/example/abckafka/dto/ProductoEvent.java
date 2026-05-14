package com.example.abckafka.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductoEvent {

    public enum TipoEvento {
        CREADO, ACTUALIZADO, ELIMINADO, STOCK_ACTUALIZADO, REACTIVADO
    }

    private TipoEvento tipoEvento;
    private Long productoId;
    private String sku;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;
    private LocalDateTime timestamp;
    private String detalle;

    public static ProductoEvent crear(TipoEvento tipo, Long id, String sku, String nombre,
                                       String categoria, BigDecimal precio, Integer stock,
                                       Boolean activo, String detalle) {
        return ProductoEvent.builder()
                .tipoEvento(tipo)
                .productoId(id)
                .sku(sku)
                .nombre(nombre)
                .categoria(categoria)
                .precio(precio)
                .stock(stock)
                .activo(activo)
                .timestamp(LocalDateTime.now())
                .detalle(detalle)
                .build();
    }
}
