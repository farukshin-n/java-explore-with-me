package ru.practicum.ewmservice.service.request;


import ru.practicum.ewmservice.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelByUser(Long userId, Long requestId);

    ParticipationRequestDto approveRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto getRequest(Long requestId);

    List<ParticipationRequestDto> getAllRequestsOfUser(Long userId);

    List<ParticipationRequestDto> getParticipationRequestsForEvent(Long userId, Long eventId);
}
