package com.example.abckafka.producer;

import com.example.abckafka.dto.ProductoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoEventProducer {

    private final KafkaTemplate<String, ProductoEvent> kafkaTemplate;

    @Value("${app.kafka.topic.productos}")
    private String productosTopic;

    @Value("${app.kafka.topic.audit}")
    private String auditTopic;

    public void enviarEvento(ProductoEvent evento) {
        String key = evento.getSku();
        log.info("Publicando evento [{}] para producto SKU={} al topic {}",
                evento.getTipoEvento(), key, productosTopic);

        CompletableFuture<SendResult<String, ProductoEvent>> future =
                kafkaTemplate.send(productosTopic, key, evento);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error publicando evento Kafka para SKU={}: {}", key, ex.getMessage());
            } else {
                log.info("Evento publicado exitosamente: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void enviarAudit(ProductoEvent evento) {
        String key = evento.getSku();
        log.info("Publicando auditoría [{}] para SKU={}", evento.getTipoEvento(), key);

        kafkaTemplate.send(auditTopic, key, evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error publicando auditoría para SKU={}: {}", key, ex.getMessage());
                    } else {
                        log.debug("Auditoría registrada: offset={}", result.getRecordMetadata().offset());
                    }
                });
    }
}
