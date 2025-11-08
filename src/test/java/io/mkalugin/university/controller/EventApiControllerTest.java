package io.mkalugin.university.controller;

import io.mkalugin.university.controller.api.EventApiController;
import io.mkalugin.university.dto.EventCreateRequest;
import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.service.EventService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тест на {@link EventApiController}
 *
 * <p> Контроллер с методами для
 * работы с событиями </p>
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Event Api controller tests")
class EventApiControllerTest {

    private static final Logger log = LoggerFactory.getLogger(EventApiControllerTest.class);

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventApiController eventApiController;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        return user;
    }

    private Event createEvent(Long id, String title, Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        Event event = new Event();
        event.setId(id);
        event.setTitle(title);
        event.setAnnotation("Test Annotation");
        event.setNotes("Test Notes");
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setReminderTime(startTime.minusHours(1));
        event.setReminderSent(false);
        event.setUser(createUser(userId));
        return event;
    }

    private EventCreateRequest createEventRequest(Long userId) {
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Test Event");
        request.setAnnotation("Test Annotation");
        request.setNotes("Test Notes");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        request.setReminderTime(LocalDateTime.now().plusDays(1).minusHours(1));
        request.setUserId(userId);
        return request;
    }

    @Test
    @Order(1)
    @DisplayName("Create event works")
    void createEventWithValidRequestShouldReturnCreatedEvent() {
        log.info("createEventWithValidRequestShouldReturnCreatedEvent test started");

        Long userId = 1L;
        EventCreateRequest request = createEventRequest(userId);
        Event expectedEvent = createEvent(1L, "Test Event", userId,
                request.getStartTime(), request.getEndTime());

        when(eventService.createEvent(request)).thenReturn(expectedEvent);

        ResponseEntity<Event> response = eventApiController.createEvent(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getTitle()).isEqualTo("Test Event");
        assertThat(response.getBody().getAnnotation()).isEqualTo("Test Annotation");
        assertThat(response.getBody().getUser().getId()).isEqualTo(userId);

        verify(eventService).createEvent(request);

        log.info("createEventWithValidRequestShouldReturnCreatedEvent test finished");
    }

    @Test
    @Order(2)
    @DisplayName("Create event with null works")
    void createEventWithServiceReturningNullShouldReturnOkWithNull() {
        log.info("createEventWithServiceReturningNullShouldReturnOkWithNull test started");

        EventCreateRequest request = createEventRequest(1L);

        when(eventService.createEvent(request)).thenReturn(null);

        ResponseEntity<Event> response = eventApiController.createEvent(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(eventService).createEvent(request);

        log.info("createEventWithServiceReturningNullShouldReturnOkWithNull test finished");
    }

    @Test
    @Order(3)
    @DisplayName("Get events by date range works")
    void getUserEventsByDateRangeWithValidParametersShouldReturnEvents() {
        log.info("getUserEventsByDateRangeWithValidParametersShouldReturnEvents test started");

        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Event event1 = createEvent(1L, "Event 1", userId,
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 11, 0));
        Event event2 = createEvent(2L, "Event 2", userId,
                LocalDateTime.of(2024, 1, 20, 14, 0),
                LocalDateTime.of(2024, 1, 20, 15, 0));
        List<Event> expectedEvents = Arrays.asList(event1, event2);

        when(eventService.getUserEventsByDateRange(userId, start, end))
                .thenReturn(expectedEvents);

        ResponseEntity<List<Event>> response = eventApiController
                .getUserEventsByDateRange(userId, start, end);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting(Event::getId)
                .containsExactly(1L, 2L);
        assertThat(response.getBody()).extracting(Event::getTitle)
                .containsExactly("Event 1", "Event 2");

        verify(eventService).getUserEventsByDateRange(userId, start, end);

        log.info("getUserEventsByDateRangeWithValidParametersShouldReturnEvents test finished");
    }

    @Test
    @Order(4)
    @DisplayName("Get events by date range with no events works")
    void getUserEventsByDateRangeWithNoEventsShouldReturnEmptyList() {
        log.info("getUserEventsByDateRangeWithNoEventsShouldReturnEmptyList test started");

        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        when(eventService.getUserEventsByDateRange(userId, start, end))
                .thenReturn(List.of());

        ResponseEntity<List<Event>> response = eventApiController
                .getUserEventsByDateRange(userId, start, end);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(eventService).getUserEventsByDateRange(userId, start, end);

        log.info("getUserEventsByDateRangeWithNoEventsShouldReturnEmptyList test finished");
    }

    @Test
    @Order(5)
    @DisplayName("Get events by date range with different users works")
    void getUserEventsByDateRangeWithDifferentUserShouldReturnUserSpecificEvents() {
        log.info("getUserEventsByDateRangeWithDifferentUserShouldReturnUserSpecificEvents  test started");

        Long userId = 2L;
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Event event = createEvent(1L, "User 2 Event", userId,
                LocalDateTime.of(2024, 1, 10, 12, 0),
                LocalDateTime.of(2024, 1, 10, 13, 0));
        List<Event> expectedEvents = List.of(event);

        when(eventService.getUserEventsByDateRange(userId, start, end))
                .thenReturn(expectedEvents);

        ResponseEntity<List<Event>> response = eventApiController
                .getUserEventsByDateRange(userId, start, end);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getUser().getId()).isEqualTo(userId);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("User 2 Event");

        verify(eventService).getUserEventsByDateRange(userId, start, end);

        log.info("getUserEventsByDateRangeWithDifferentUserShouldReturnUserSpecificEvents test finished");
    }

    @Test
    @Order(6)
    @DisplayName("Get upcoming events works")
    void getUpcomingUserEventsWithDefaultDaysAheadShouldReturnUpcomingEvents() {
        log.info("getUpcomingUserEventsWithDefaultDaysAheadShouldReturnUpcomingEvents test started");

        Long userId = 1L;
        int daysAhead = 30;

        Event event1 = createEvent(1L, "Upcoming Event 1", userId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1));
        Event event2 = createEvent(2L, "Upcoming Event 2", userId,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(5).plusHours(2));
        List<Event> expectedEvents = Arrays.asList(event1, event2);

        when(eventService.getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(expectedEvents);

        ResponseEntity<List<Event>> response = eventApiController
                .getUpcomingUserEvents(userId, daysAhead);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).extracting(Event::getTitle)
                .containsExactly("Upcoming Event 1", "Upcoming Event 2");

        verify(eventService).getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        log.info("getUpcomingUserEventsWithDefaultDaysAheadShouldReturnUpcomingEvents  test finished");
    }

    @Test
    @Order(7)
    @DisplayName("Get upcoming events with custom days works")
    void getUpcomingUserEventsWithCustomDaysAheadShouldReturnUpcomingEvents() {
        log.info("getUpcomingUserEventsWithCustomDaysAheadShouldReturnUpcomingEvents test started");

        Long userId = 1L;
        int daysAhead = 7;

        Event event = createEvent(1L, "Weekly Event", userId,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(1));
        List<Event> expectedEvents = List.of(event);

        when(eventService.getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(expectedEvents);

        ResponseEntity<List<Event>> response = eventApiController
                .getUpcomingUserEvents(userId, daysAhead);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Weekly Event");

        verify(eventService).getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        log.info("getUpcomingUserEventsWithCustomDaysAheadShouldReturnUpcomingEvents test finished");
    }

    @Test
    @Order(8)
    @DisplayName("Get upcoming events with zero ahead days works")
    void getUpcomingUserEventsWithZeroDaysAheadShouldReturnEmptyList() {
        log.info("getUpcomingUserEventsWithZeroDaysAheadShouldReturnEmptyList test started");

        Long userId = 1L;
        int daysAhead = 0;

        when(eventService.getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(List.of());

        ResponseEntity<List<Event>> response = eventApiController
                .getUpcomingUserEvents(userId, daysAhead);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(eventService).getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );

        log.info("getUpcomingUserEventsWithZeroDaysAheadShouldReturnEmptyList test finished");
    }

    @Test
    @Order(9)
    @DisplayName("Get upcoming events with no events works")
    void getUpcomingUserEventsWithNoUpcomingEventsShouldReturnEmptyList() {
        log.info("getUpcomingUserEventsWithNoUpcomingEventsShouldReturnEmptyList test started");

        Long userId = 1L;
        int daysAhead = 30;

        when(eventService.getUserEventsByDateRange(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(List.of());

        ResponseEntity<List<Event>> response = eventApiController
                .getUpcomingUserEvents(userId, daysAhead);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        log.info("getUpcomingUserEventsWithNoUpcomingEventsShouldReturnEmptyList test finished");
    }

    @Test
    @Order(10)
    @DisplayName("Create event with all fields works")
    void createEventWithAllFieldsShouldReturnEventWithAllFields() {
        log.info("createEventWithAllFieldsShouldReturnEventWithAllFields test started");

        Long userId = 1L;
        EventCreateRequest request = createEventRequest(userId);

        Event expectedEvent = new Event();
        expectedEvent.setId(1L);
        expectedEvent.setTitle(request.getTitle());
        expectedEvent.setAnnotation(request.getAnnotation());
        expectedEvent.setNotes(request.getNotes());
        expectedEvent.setStartTime(request.getStartTime());
        expectedEvent.setEndTime(request.getEndTime());
        expectedEvent.setReminderTime(request.getReminderTime());
        expectedEvent.setReminderSent(false);
        expectedEvent.setUser(createUser(userId));

        when(eventService.createEvent(request)).thenReturn(expectedEvent);

        ResponseEntity<Event> response = eventApiController.createEvent(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getBody().getAnnotation()).isEqualTo(request.getAnnotation());
        assertThat(response.getBody().getNotes()).isEqualTo(request.getNotes());
        assertThat(response.getBody().getStartTime()).isEqualTo(request.getStartTime());
        assertThat(response.getBody().getEndTime()).isEqualTo(request.getEndTime());
        assertThat(response.getBody().getReminderTime()).isEqualTo(request.getReminderTime());
        assertThat(response.getBody().isReminderSent()).isFalse();

        log.info("createEventWithAllFieldsShouldReturnEventWithAllFields test finished");
    }

    @Test
    @Order(11)
    @DisplayName("Get events with full data works")
    void getUserEventsByDateRangeWithEventsContainingAnnotationsAndNotesShouldReturnFullEventData() {
        log.info("getUserEventsByDateRangeWithEventsContainingAnnotationsAndNotesShouldReturnFullEventData test started");

        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        Event event = createEvent(1L, "Meeting", userId,
                LocalDateTime.of(2024, 1, 15, 10, 0),
                LocalDateTime.of(2024, 1, 15, 11, 0));
        event.setAnnotation("Important meeting with client");
        event.setNotes("Prepare presentation and reports");
        event.setReminderTime(LocalDateTime.of(2024, 1, 15, 9, 30));

        List<Event> expectedEvents = List.of(event);

        when(eventService.getUserEventsByDateRange(userId, start, end))
                .thenReturn(expectedEvents);

        ResponseEntity<List<Event>> response = eventApiController
                .getUserEventsByDateRange(userId, start, end);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getAnnotation()).isEqualTo("Important meeting with client");
        assertThat(response.getBody().get(0).getNotes()).isEqualTo("Prepare presentation and reports");
        assertThat(response.getBody().get(0).getReminderTime()).isEqualTo(LocalDateTime
                .of(2024, 1, 15, 9, 30));

        log.info("getUserEventsByDateRangeWithEventsContainingAnnotationsAndNotesShouldReturnFullEventData test finished");
    }
}
