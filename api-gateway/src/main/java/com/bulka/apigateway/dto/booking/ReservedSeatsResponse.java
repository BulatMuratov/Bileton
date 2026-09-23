package com.bulka.apigateway.dto.booking;

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
public class ReservedSeatsResponse {
    private UUID eventId;
    private List<UUID> reservedEventSeatsId;
}
