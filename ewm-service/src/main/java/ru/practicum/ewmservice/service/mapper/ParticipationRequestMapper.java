package ru.practicum.ewmservice.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.ParticipationRequest;
import ru.practicum.ewmservice.model.ParticipationRequestState;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class ParticipationRequestMapper {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ParticipationRequest toRequest(ParticipationRequestDto dto) {
        User requester = userRepository.findById(dto.getRequester()).orElseThrow(() -> new EntityNotFoundException(
                String.format("There isn't user with id %d in this database.", dto.getRequester())
        ));
        Event event = eventRepository.findById(dto.getEvent()).orElseThrow(() -> new EntityNotFoundException(
                        String.format("There isn't event with id=%d in repository.", dto.getEvent())
                ));
        return new ParticipationRequest(
                dto.getId(),
                requester,
                event,
                ParticipationRequestState.PENDING,
                dto.getCreated()
        );
    }

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
