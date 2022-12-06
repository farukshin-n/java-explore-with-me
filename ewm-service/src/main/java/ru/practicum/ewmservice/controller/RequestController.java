package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.service.request.ParticipationRequestService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
public class RequestController {
    private final ParticipationRequestService requestService;

    @PostMapping("/requests")
    public ParticipationRequestDto addRequest(@PathVariable @PositiveOrZero Long userId,
                                              @RequestParam @PositiveOrZero Long eventId) {
        log.info("Getting participation request to event id={} from user with id={}.", eventId, userId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getAllRequestsOfUser(@PathVariable @PositiveOrZero Long userId) {
        log.info("Getting request for getting participation requests from user with id={}.", userId);
        return requestService.getAllRequestsOfUser(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsForEvent(
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable @PositiveOrZero Long eventId) {
        log.info("Getting request for getting participation requests for event id={} added by user id={}.",
                userId,
                eventId);
        return requestService.getParticipationRequestsForEvent(eventId, userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelByUser(
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable @PositiveOrZero Long requestId) {
        log.info("Get request for cancelling participation request with id={} from user with id={}.", requestId, userId);
        return requestService.cancelByUser(userId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto cancelRequest(
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable @PositiveOrZero Long reqId) {
        log.info("Get request for cancelling participation request id={} to event id={} from user id={}.",
                reqId, eventId, userId);
        return requestService.cancelRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto approveRequest(
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable @PositiveOrZero Long reqId) {
        log.info("Get request for approving participation request id={} to event id={} from user id={}.",
                reqId, eventId, userId);
        return requestService.approveRequest(userId, eventId, reqId);
    }
}
