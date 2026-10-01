package com.bulka.paymentservice.config;

import com.bulka.paymentservice.kafka.event.PaymentRefundEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, PaymentRefundEvent> eventConsumerFactory(
            KafkaProperties properties) {

        Map<String, Object> props = new HashMap<>(properties.buildConsumerProperties());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        JacksonJsonDeserializer<PaymentRefundEvent> deserializer =
                new JacksonJsonDeserializer<>(PaymentRefundEvent.class);

        deserializer.addTrustedPackages("com.bulka.paymentservice.kafka.event");
        
        System.out.println("KAFKA BOOTSTRAP = [" + bootstrapServers + "]");
        System.out.println("KAFKA PROPS = " + props);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRefundEvent>
    eventKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentRefundEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentRefundEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}