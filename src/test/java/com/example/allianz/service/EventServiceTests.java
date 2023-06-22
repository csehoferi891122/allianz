package com.example.allianz.service;

import com.example.allianz.domain.Event;
import com.example.allianz.dto.CreateEvent;
import com.example.allianz.exception.EventTimeException;
import com.example.allianz.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(eventService, "startDay", "09:00");
        ReflectionTestUtils.setField(eventService, "finishDay", "17:00");
    }

    @Test
    void createEvent_ValidEvent_SuccessfullyCreatesEvent() throws EventTimeException {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"09:00", "10:00");

        Event event = new Event();
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(createEvent);

        Assertions.assertEquals(event, createdEvent);
    }

    @Test
    void createEvent_ValidEventWith4People_SuccessfullyCreatesEvent() throws EventTimeException {
        CreateEvent createEvent0 = new CreateEvent("Geza", LocalDate.now(),"09:00", "11:00");
        CreateEvent createEvent1 = new CreateEvent("Antal", LocalDate.now(),"11:00", "13:00");
        CreateEvent createEvent2 = new CreateEvent("Maja", LocalDate.now(),"13:00", "15:00");
        CreateEvent createEvent3 = new CreateEvent("Olga", LocalDate.now(),"15:00", "17:00");

        Event event = new Event();
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.createEvent(createEvent0);
        eventService.createEvent(createEvent1);
        eventService.createEvent(createEvent2);
        eventService.createEvent(createEvent3);

        verify(eventRepository, times(4)).save(any(Event.class));
    }

    @Test
    void createEvent_InvalidStartTime_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"08:10", "10:00");

        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_InvalidFinishTime_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"09:00", "10:25");
        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_StartTimeAfterFinishTime_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"09:00", "08:00");
        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_ExceedsMaxReservationTime_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"09:00", "12:30");
        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_StartTimeOutsideWorkingHours_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"08:00", "09:00");
        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_FinishTimeOutsideWorkingHours_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"16:00", "18:00");
        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_StartTimeOverlapsExistingEvent_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"10:00", "11:00");
        List<Event> events = Arrays.asList(
                createMockEvent("09:30", "10:30"),
                createMockEvent("11:30", "12:30")
        );
        when(eventRepository.getEventsByEventDate(any(LocalDate.class))).thenReturn(events);

        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void createEvent_FinishTimeOverlapsExistingEvent_ThrowsEventTimeException() {
        CreateEvent createEvent = new CreateEvent("Geza", LocalDate.now(),"10:30", "12:00");

        List<Event> events = Arrays.asList(
                createMockEvent("09:30", "10:30"),
                createMockEvent("11:30", "12:30")
        );
        when(eventRepository.getEventsByEventDate(any(LocalDate.class))).thenReturn(events);

        Assertions.assertThrows(EventTimeException.class, () -> eventService.createEvent(createEvent));
    }

    @Test
    void getEventsByDate_ValidDate_ReturnsListOfEvents() {
        LocalDate date = LocalDate.now();
        List<Event> expectedEvents = Arrays.asList(
                createMockEvent("09:00", "10:00"),
                createMockEvent("11:00", "12:00")
        );
        when(eventRepository.getEventsByEventDate(date)).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getEventsByDate(date);

        Assertions.assertEquals(expectedEvents, actualEvents);
    }

    private Event createMockEvent(String startTime, String finishTime) {
        Event event = new Event();
        event.setStart(startTime);
        event.setFinish(finishTime);
        return event;
    }
}
