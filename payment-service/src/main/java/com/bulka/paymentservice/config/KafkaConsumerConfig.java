package com.bulka.paymentservice.config;


import com.bulka.paymentservice.kafka.event.PaymentRefundEvent;
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
    public ConsumerFactory<String, PaymentRefundEvent> eventConsumerFactory(
            KafkaProperties properties) {

        JacksonJsonDeserializer<PaymentRefundEvent> deserializer =
                new JacksonJsonDeserializer<>(PaymentRefundEvent.class);

        deserializer.addTrustedPackages("com.bulka.bookingservice.kafka.event");

        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties(),
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
