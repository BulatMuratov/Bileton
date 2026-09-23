package com.bulka.bookingservice.config;

import com.bulka.bookingservice.kafka.dto.EventUpdatedEvent;
import com.bulka.bookingservice.kafka.dto.PaymentSucceededEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, EventUpdatedEvent> eventConsumerFactory(
            KafkaProperties properties) {

        JacksonJsonDeserializer<EventUpdatedEvent> deserializer =
                new JacksonJsonDeserializer<>(EventUpdatedEvent.class);

        deserializer.addTrustedPackages("com.bulka.bookingservice.kafka.dto");

        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventUpdatedEvent>
    eventKafkaListenerContainerFactory(
            ConsumerFactory<String, EventUpdatedEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, EventUpdatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentSucceededEvent> paymentConsumerFactory(
            KafkaProperties properties) {

        JacksonJsonDeserializer<PaymentSucceededEvent> deserializer =
                new JacksonJsonDeserializer<>(PaymentSucceededEvent.class);

        deserializer.addTrustedPackages("com.bulka.bookingservice.kafka.dto");

        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSucceededEvent>
    paymentKafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentSucceededEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentSucceededEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
