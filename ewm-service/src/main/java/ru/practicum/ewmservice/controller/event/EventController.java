package ru.practicum.ewmservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> searchEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false, defaultValue = "false") Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        return eventService.getAllEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @PostMapping("/users/{userId}/events")
    public EventFullDto addEvent(@PathVariable @Positive Long userId,
                                 @RequestBody @Valid NewEventDto eventDto) {
        log.info("Get request for saving event {} from user with id={}", eventDto.getTitle(), userId);
        return eventService.addEvent(eventDto, userId);
    }

    @PatchMapping("/users/{userId}/events")
    public EventFullDto updateEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid UpdateEventRequest eventRequest) {
        log.info("Get request for updating event with id={} from user with id={}", eventRequest.getEventId(), userId);
        return eventService.updateEvent(userId, eventRequest);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto cancelEventByInitiator(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId) {
        log.info("Get request for cancelling event with id={} by initiator (id={}).", eventId, userId);
        return eventService.cancelEventByInitiator(userId, eventId);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEvent(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("Get request for getting event with id={}.", id);
        return eventService.getEvent(id, request);
    }

    @GetMapping("users/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable @Positive Long userId,
                                     @PathVariable @Positive Long eventId) {
        log.info("Get request for getting event with id={} from user with id={}.", eventId, userId);
        return eventService.getEventByUser(userId, eventId);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getAllEventsByUser(
            @PathVariable @Positive Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Get request for getting all events added by user with id={}", userId);
        return eventService.getAllEventsByUser(userId, from, size);
    }

    @DeleteMapping("/events/{eventId}")
    public void deleteEventById(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
