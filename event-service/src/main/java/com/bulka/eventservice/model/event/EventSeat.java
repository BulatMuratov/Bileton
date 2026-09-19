package com.bulka.eventservice.model.event;


import com.bulka.eventservice.model.venue.Seat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
        name = "event_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_seats_event_section_seat",
                        columnNames = {
                                "event_section_id",
                                "seat_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_section_id", nullable = false)
    private EventSection eventSection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventSeatStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}