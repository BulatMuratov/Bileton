package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.request.VenueInfoRequestDto;
import com.bulka.eventservice.dto.request.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.response.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.VenueInfoResponseDto;
import com.bulka.eventservice.service.VenueService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
@AllArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueDetailsResponseDto> createVenue(@RequestBody VenueDetailsRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venueService.createVenue(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<VenueInfoResponseDto>> getAllVenue(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getAllVenueInfo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueInfoResponseDto> getVenue(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getVenueInfoById(id));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<?> getSeatsOfVenue(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getSeatsByVenueId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateVenue(@PathVariable UUID id, @RequestBody VenueInfoRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.updateVenue(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable UUID id) {
        venueService.deleteVenue(id);
    }

}
