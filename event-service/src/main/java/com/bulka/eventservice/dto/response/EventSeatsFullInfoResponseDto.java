package com.bulka.eventservice.dto.response;

import com.bulka.eventservice.dto.VenueSizeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSeatsFullInfoResponseDto {
    private UUID eventId;
    private VenueSizeDto venue;
    private List<EventSeatFullInfoResponseDto> seats;
}
