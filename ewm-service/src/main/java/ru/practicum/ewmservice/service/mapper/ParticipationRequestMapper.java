package ru.practicum.ewmservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.ParticipationRequest;

@Component
@RequiredArgsConstructor
public class ParticipationRequestMapper {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getRequester().getId(),
                request.getEvent().getId(),
                request.getStatus().toString(),
                request.getCreated()
        );
    }
}
