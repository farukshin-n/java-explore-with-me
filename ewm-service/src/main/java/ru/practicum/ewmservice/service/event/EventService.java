package ru.practicum.ewmservice.service.event;

import ru.practicum.ewmservice.dto.event.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto eventDto, Long userId);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

    EventFullDto getEventByUser(Long userId, Long eventId);

    List<EventShortDto> getAllEvents(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     String sort,
                                     int from,
                                     int size,
                                     HttpServletRequest request);

    List<EventFullDto> getAllEventsByAdmin(List<Long> users,
                                            List<String> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size);

    List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto updateEvent(long userId, UpdateEventRequest eventRequest);

    void deleteEvent(long eventId);

    EventFullDto cancelEventByInitiator(Long userId, Long eventId);

    EventFullDto cancelEventByAdmin(Long eventId);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishEvent(Long eventId);
}
