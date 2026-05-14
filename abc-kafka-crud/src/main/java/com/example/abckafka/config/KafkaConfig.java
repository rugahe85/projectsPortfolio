package com.example.abckafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.productos}")
    private String productosTopic;

    @Value("${app.kafka.topic.audit}")
    private String auditTopic;

    @Bean
    public NewTopic productosTopic() {
        return TopicBuilder.name(productosTopic)
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name(auditTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
