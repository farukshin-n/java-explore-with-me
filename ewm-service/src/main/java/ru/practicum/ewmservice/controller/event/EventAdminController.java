package ru.practicum.ewmservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.Update;
import ru.practicum.ewmservice.dto.AdminUpdateEventRequest;
import ru.practicum.ewmservice.dto.EventFullDto;
import ru.practicum.ewmservice.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @PutMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(
            @PathVariable @Positive Long eventId,
            @RequestBody @Validated({Update.class}) AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Get request for ");
        return eventService.updateEventByAdmin(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable @Positive Long eventId) {
        log.info("Getting request for publish event with id={}", eventId);
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto cancelEventByAdmin(@PathVariable @Positive Long eventId) {
        log.info("Getting request for cancel event with id={} by admin.", eventId);
        return eventService.cancelEventByAdmin(eventId);
    }

    @GetMapping
    public List<EventFullDto> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        log.info("Get request from admin to show all users {}.", users);

        return eventService.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
