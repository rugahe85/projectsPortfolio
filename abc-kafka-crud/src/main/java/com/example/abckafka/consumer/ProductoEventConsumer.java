package com.example.abckafka.consumer;

import com.example.abckafka.dto.ProductoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductoEventConsumer {

    @KafkaListener(
        topics = "${app.kafka.topic.productos}",
        groupId = "abc-crud-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoProducto(
            @Payload ProductoEvent evento,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║  Evento recibido de Kafka                       ║");
        log.info("╠══════════════════════════════════════════════════╣");
        log.info("║  Topic:     {}", topic);
        log.info("║  Partition: {}", partition);
        log.info("║  Offset:    {}", offset);
        log.info("║  Tipo:      {}", evento.getTipoEvento());
        log.info("║  SKU:       {}", evento.getSku());
        log.info("║  Producto:  {}", evento.getNombre());
        log.info("║  Timestamp: {}", evento.getTimestamp());
        log.info("╚══════════════════════════════════════════════════╝");

        switch (evento.getTipoEvento()) {
            case CREADO -> procesarCreacion(evento);
            case ACTUALIZADO -> procesarActualizacion(evento);
            case ELIMINADO -> procesarEliminacion(evento);
            case STOCK_ACTUALIZADO -> procesarStockActualizado(evento);
            case REACTIVADO -> procesarReactivacion(evento);
        }
    }

    @KafkaListener(
        topics = "${app.kafka.topic.audit}",
        groupId = "abc-audit-group"
    )
    public void consumirAudit(@Payload ProductoEvent evento) {
        log.info("[AUDIT] {} | SKU={} | {} | {}",
                evento.getTipoEvento(), evento.getSku(),
                evento.getDetalle(), evento.getTimestamp());
    }

    private void procesarCreacion(ProductoEvent evento) {
        log.info("Procesando ALTA de producto: {} (SKU: {}, Precio: ${})",
                evento.getNombre(), evento.getSku(), evento.getPrecio());
    }

    private void procesarActualizacion(ProductoEvent evento) {
        log.info("Procesando CAMBIO de producto: {} (SKU: {})",
                evento.getNombre(), evento.getSku());
    }

    private void procesarEliminacion(ProductoEvent evento) {
        log.info("Procesando BAJA de producto: {} (SKU: {})",
                evento.getNombre(), evento.getSku());
    }

    private void procesarStockActualizado(ProductoEvent evento) {
        log.info("Procesando actualización de stock: {} → {} unidades",
                evento.getSku(), evento.getStock());
    }

    private void procesarReactivacion(ProductoEvent evento) {
        log.info("Procesando REACTIVACIÓN de producto: {} (SKU: {})",
                evento.getNombre(), evento.getSku());
    }
}
