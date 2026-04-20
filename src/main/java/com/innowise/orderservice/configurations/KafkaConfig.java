package com.innowise.orderservice.configurations;

import com.innowise.orderservice.service.dto.PaymentResponseDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, PaymentResponseDto> consumerFactory(
            @Value("${spring.kafka.bootstrap-servers}")  String server,
            @Value("${spring.kafka.consumer.group-id}") String groupId

    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        var deserializer = new JacksonJsonDeserializer<>(PaymentResponseDto.class);
        deserializer.addTrustedPackages("*");

        var errorHandlingDeserializer = new ErrorHandlingDeserializer<>(deserializer);

        return  new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                errorHandlingDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentResponseDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentResponseDto> consumerFactory
    ) {
        var containerFactory =
                new ConcurrentKafkaListenerContainerFactory<String, PaymentResponseDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);

        return containerFactory;
    }
}
