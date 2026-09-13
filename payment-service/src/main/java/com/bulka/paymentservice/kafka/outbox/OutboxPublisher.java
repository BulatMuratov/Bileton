package com.bulka.paymentservice.kafka.outbox;

import com.bulka.paymentservice.model.OutboxEvent;
import com.bulka.paymentservice.model.OutboxEventStatus;
import com.bulka.paymentservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishEvents(){
        List<OutboxEvent> outboxEventList = outboxEventRepository.findTop100ByStatusOrderByCreatedAtDesc(OutboxEventStatus.NEW);

        for(OutboxEvent outboxEvent : outboxEventList){
//            publish(outboxEvent);
        }
    }

//    private void publish();
}
