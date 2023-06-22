package com.example.allianz.controller;

import com.example.allianz.domain.Event;
import com.example.allianz.dto.CreateEvent;
import com.example.allianz.exception.EventTimeException;
import com.example.allianz.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("event")
public class EventController {

    private final EventService eventService;

    @PutMapping
    public Event createEvent(@RequestBody CreateEvent createEvent) throws EventTimeException {
        return eventService.createEvent(createEvent);
    }

    @GetMapping("/{date}")
    public List<Event> getEventsByDate(@PathVariable LocalDate date) {
        return eventService.getEventsByDate(date);
    }

    @GetMapping("/for-week")
    public List<Event> getEventsForWeek() {
        return eventService.getEventsForWeek();
    }

}
