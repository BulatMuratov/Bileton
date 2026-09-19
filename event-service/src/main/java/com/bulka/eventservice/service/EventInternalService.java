//package com.bulka.eventservice.service;
//
//import com.bulka.eventservice.dto.internal.request.EventSeatInfoDto;
//import com.bulka.eventservice.exception.event.EventNotFoundException;
//import com.bulka.eventservice.exception.event.EventSeatNotFoundException;
//import com.bulka.eventservice.model.event.Event;
//import com.bulka.eventservice.model.event.EventSeat;
//import com.bulka.eventservice.repository.event.EventRepository;
//import com.bulka.eventservice.repository.event.EventSeatRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class EventInternalService {
//
//    private final EventRepository eventRepository;
//    private final EventSeatRepository eventSeatRepository;
//
//
//    @Transactional
//    public boolean sellSeats(UUID eventId, List<UUID> eventSeatIds){
//        int updated = eventSeatRepository.sellAvailableSeats(
//                eventId,
//                eventSeatIds
//        );
//
//        return updated == eventSeatIds.size();
//    }
//
//    @Transactional(readOnly = true)
//    public List<EventSeatInfoDto> getEventSeats(UUID eventId, List<UUID> eventSeatIds) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new EventNotFoundException( "Event with id " + eventId + " not found" ) );
//
//        List<EventSeat> eventSeats = eventSeatRepository.findAllByEventIdAndIdIn(eventId, eventSeatIds);
//
//        if(eventSeats.size() != eventSeatIds.size()) {
//            throw new EventSeatNotFoundException("Some event seats do not belong to event " + eventId);
//        }
//
//        return eventSeats.stream()
//                .map(eventSeat -> EventSeatInfoDto.builder()
//                        .eventSeatId(eventSeat.getId())
//                        .price(eventSeat.getPrice())
//                        .build())
//                .toList();
//    }
//}
