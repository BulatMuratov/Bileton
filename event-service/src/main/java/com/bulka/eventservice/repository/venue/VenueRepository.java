package com.bulka.eventservice.repository.venue;

import com.bulka.eventservice.model.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueRepository extends JpaRepository<Venue, UUID> {

}
