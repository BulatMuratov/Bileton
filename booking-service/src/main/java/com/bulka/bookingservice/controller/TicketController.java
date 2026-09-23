package com.bulka.bookingservice.controller;

import com.bulka.bookingservice.dto.response.TicketDetailsResponse;
import com.bulka.bookingservice.dto.response.TicketSummaryResponse;
import com.bulka.bookingservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // temp
    @PostMapping("/{bookingId}")
    public void createTicket(@PathVariable UUID bookingId){
        ticketService.createTickets(bookingId);
    }

    @GetMapping
    public ResponseEntity<List<TicketSummaryResponse>> getTicketsByUser(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ticketService.getTicketsByUser(userId));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDetailsResponse> getTicketById(
            Authentication authentication,
            @PathVariable("ticketId") UUID ticketId
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ticketService.getTicket(userId, ticketId));
    }


}
