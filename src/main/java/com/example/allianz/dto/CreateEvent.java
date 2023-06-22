package com.example.allianz.dto;

import com.example.allianz.domain.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateEvent(String users, LocalDate eventDate, String start, String finish) {

    public static Event convertToEvent(CreateEvent createEvent) {
        return Event.builder().users(createEvent.users).eventDate(createEvent.eventDate).start(createEvent.start)
                .finish(createEvent.finish).build();
    }
}
