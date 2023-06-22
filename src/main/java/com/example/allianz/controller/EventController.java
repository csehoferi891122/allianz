package com.example.allianz.controller;

import com.example.allianz.domain.Event;
import com.example.allianz.dto.CreateEvent;
import com.example.allianz.exception.EventTimeException;
import com.example.allianz.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Allianz test feladatok Rest interfész
 *
 * @author Ferenc Csehó-Kovács
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("event")
public class EventController {

    private final EventService eventService;

    /**
     * <p>Meeting létrehozása</p>
     * @param createEvent Meeting létrehozásához szükséges paramtéereket tartalmazó record
     * @exception EventTimeException
     * @return létrehozott meeting
     */
    @PutMapping
    public Event createEvent(@RequestBody CreateEvent createEvent) throws EventTimeException {
        return eventService.createEvent(createEvent);
    }

    /**
     * <p>Meetingek lekérdezése dátum alapján</p>
     * @param date Meeting napja
     * @return adott napi meetingek
     */
    @GetMapping("/{date}")
    public List<Event> getEventsByDate(@PathVariable LocalDate date) {
        return eventService.getEventsByDate(date);
    }

    /**
     * <p>Addot heti meetingek lekérdezése</p>
     * @return adott heti meetingek
     */
    @GetMapping("/for-week")
    public List<Event> getEventsForWeek() {
        return eventService.getEventsForWeek();
    }

}
