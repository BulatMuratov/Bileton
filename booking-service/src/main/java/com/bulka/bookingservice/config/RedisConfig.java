package com.bulka.bookingservice.config;

import com.bulka.bookingservice.listener.BookingExpirationListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfig {

    @Bean
    public DefaultRedisScript<Long> reserveScript(
            @Value("classpath:scripts/redis/reserve.lua") Resource resource) throws IOException {
        String script = StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
        );
        return new DefaultRedisScript<>(script, Long.class);
    }

    @Bean
    public DefaultRedisScript<Long> releaseScript(
            @Value("classpath:scripts/redis/release.lua") Resource resource) throws IOException {
        String script = StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
        );
        return new DefaultRedisScript<>(script, Long.class);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            BookingExpirationListener bookingExpirationListener
    ) {
      RedisMessageListenerContainer container = new RedisMessageListenerContainer();
      container.setConnectionFactory(redisConnectionFactory);

      container.addMessageListener(
              new MessageListenerAdapter(bookingExpirationListener, "onMessage"),
              new PatternTopic("__keyevent@*:expired")
      );

      return container;
    }


}
