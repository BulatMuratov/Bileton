package com.bulka.bookingservice.model.ticket;

import com.bulka.bookingservice.model.booking.Booking;
import com.bulka.bookingservice.model.booking.BookingItem;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tickets_booking_item",
                        columnNames = "booking_item_id"
                ),
                @UniqueConstraint(
                        name = "uk_tickets_ticket_number",
                        columnNames = "ticket_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "booking_item_id",
            nullable = false,
            unique = true
    )
    private BookingItem bookingItem;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;


    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_start_at", nullable = false)
    private OffsetDateTime eventStartAt;

    @Column(name = "event_end_at", nullable = false)
    private OffsetDateTime eventEndAt;

    @Column(name = "venue_name", nullable = false)
    private String venueName;


    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;


    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}