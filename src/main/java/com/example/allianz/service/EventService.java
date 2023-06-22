package com.example.allianz.service;

import com.example.allianz.domain.Event;
import com.example.allianz.dto.CreateEvent;
import com.example.allianz.exception.EventTimeException;
import com.example.allianz.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final String PATTERN =
            "([01]?[0-9]|2[0-3]):(00|30)";

    private final EventRepository eventRepository;

    @Value("${event.start-day:09:00}")
    private String startDay;

    @Value("${event.finish-day:17:00}")
    private String finishDay;

    @Transactional
    public Event createEvent(CreateEvent createEvent) throws EventTimeException {
        checkStartAndFinish(createEvent);
        return eventRepository.save(CreateEvent.convertToEvent(createEvent));
    }

    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.getEventsByEventDate(date);
    }

    public List<Event> getEventsForWeek() {
        LocalDate firstDayOfWeek = LocalDate.now();
        DayOfWeek weekStart = DayOfWeek.MONDAY;
        firstDayOfWeek =  firstDayOfWeek.with(TemporalAdjusters.previousOrSame(weekStart));
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(5);
        return eventRepository.findAllByEventDateGreaterThanEqualAndEventDateLessThanEqual(firstDayOfWeek, lastDayOfWeek);
    }

    private void checkStartAndFinish(CreateEvent createEvent) throws EventTimeException {
        LocalTime startTime = LocalTime.parse(createEvent.start());
        LocalTime finishTime = LocalTime.parse(createEvent.finish());
        LocalTime startDayTime = LocalTime.parse(startDay);
        LocalTime finishDayTime = LocalTime.parse(finishDay);

        Pattern pattern = Pattern.compile(PATTERN);
        if (!pattern.matcher(createEvent.start()).find() || !pattern.matcher(createEvent.finish()).find()) {
            throw new EventTimeException("Foglalni csak egésztől vagy féltől kezdődően lehet. pl: 09:00 vagy 09:30");
        }
        if (finishTime.isBefore(startTime)) {
            throw new EventTimeException("Start hamarabb legyen, mint a finish");
        }
        if (startTime.plusHours(3).isBefore(finishTime)) {
            throw new EventTimeException("Túllépted a 3 órányi foglalás időt");
        }
        if ((startTime.isBefore(startDayTime) || startTime.isAfter(finishDayTime)) ||
                (finishTime.isBefore(startDayTime) || finishTime.isAfter(finishDayTime))) {
            throw new EventTimeException("Csak 09:00 és 17:00 között lehet foglalni");
        }
        checkEventDateIsFree(startTime, finishTime, createEvent.eventDate());
    }

    private void checkEventDateIsFree(LocalTime startTime, LocalTime finishTime, LocalDate eventDate) throws EventTimeException {
        List<Event> events = getEventsByDate(eventDate);
        for (Event event : events) {
            LocalTime reservedStartTime = LocalTime.parse(event.getStart());
            LocalTime reservedFinishTime = LocalTime.parse(event.getFinish());
            if (startTime.isAfter(reservedStartTime) && startTime.isBefore(reservedFinishTime)) {
                throw new EventTimeException("Kezdési idő ütközés");
            } else if (finishTime.isAfter(reservedStartTime) && finishTime.isBefore(reservedFinishTime)) {
                throw new EventTimeException("Végzési idő ütközés");
            }
        }
    }
}
