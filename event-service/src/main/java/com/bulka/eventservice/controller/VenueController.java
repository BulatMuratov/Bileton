package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueInfoRequestDto;
import com.bulka.eventservice.dto.response.venue.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueSummaryResponseDto;
import com.bulka.eventservice.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueDetailsResponseDto> createVenue(
            @RequestBody VenueDetailsRequestDto requestDto,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venueService.createVenue(requestDto, idempotencyKey));
    }

    @GetMapping
    public ResponseEntity<List<VenueSummaryResponseDto>> getAllVenues(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getAllVenuesSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDetailsResponseDto> getFullVenueInfo(
            @PathVariable UUID id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getVenueDetailsById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VenueSummaryResponseDto> updateVenueInfo(
            @PathVariable UUID id,
            @RequestBody VenueInfoRequestDto request
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.updateVenueInfo(id, request));

    }

//    @GetMapping("/{id}/seats")
//    public ResponseEntity<?> getSeatsOfVenue(@PathVariable UUID id) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(venueService.getSeatsByVenueId(id));
//    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<?> updateVenue(@PathVariable UUID id, @RequestBody VenueInfoRequestDto requestDto) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(venueService.updateVenue(id, requestDto));
//    }

}
