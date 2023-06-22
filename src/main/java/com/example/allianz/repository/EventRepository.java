package com.example.allianz.repository;

import com.example.allianz.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> getEventsByEventDate(LocalDate eventDate);

    List<Event> findAllByEventDateGreaterThanEqualAndEventDateLessThanEqual(LocalDate startDate, LocalDate endDate);
}
