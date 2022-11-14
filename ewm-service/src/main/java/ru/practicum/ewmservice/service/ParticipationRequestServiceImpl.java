package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.ForbiddenException;
import ru.practicum.ewmservice.exception.ValidateConflictException;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.ParticipationRequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.mapper.ParticipationRequestMapper;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestMapper mapper;
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = getUserFromRepo(userId);
        Event event = getEventFromRepo(eventId);
        if (requestRepository.findByEvent_IdAndRequester_Id(event.getId(), requester.getId()).isPresent()) {
            throw new ForbiddenException(
                    String.format("Participation request for event with id=%d already exists.", event.getId())
            );
        }
        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ValidateConflictException(String.format("User with id=%d cannot request for his own event (id=%d)",
                            requester.getId(),
                            event.getId())
            );
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidateConflictException(
                    "User cannot add request for unpublished event."
            );
        }
        Integer confirmedParticipationRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                ParticipationRequestState.CONFIRMED).size();
        if (event.getParticipantLimit().equals(confirmedParticipationRequests)) {
            throw new ValidateConflictException("There isn't more places in this event.");
        }
        ParticipationRequestState state = ParticipationRequestState.PENDING;
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            state = ParticipationRequestState.CONFIRMED;
        }

        ParticipationRequest requestToAdd = new ParticipationRequest(null,
                requester,
                event,
                state,
                LocalDateTime.now());
        ParticipationRequest savedRequest = requestRepository.save(requestToAdd);
        log.info("Participation request with id={} saved successfully.", savedRequest.getId());
        return mapper.toDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelByUser(Long userId, Long requestId) {
        User user = getUserFromRepo(userId);
        ParticipationRequest request = getRequestFromRepo(requestId);
        if (!user.getId().equals(request.getRequester().getId())) {
            throw new ForbiddenException(String.format("User with id=%d haven't access to cancel event with id=%d",
                    user.getId(),
                    request.getRequester().getId()));
        }
        request.setStatus(ParticipationRequestState.CANCELED);
        ParticipationRequest savedRequest = requestRepository.save(request);
        log.info("Request with id={} to event with id={} successfully canceled by user with id={}.",
                request.getId(), request.getEvent().getId(), user.getId());
        return mapper.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto approveRequest(Long userId, Long eventId, Long reqId) {
        User user = getUserFromRepo(userId);
        Event event = getEventFromRepo(eventId);
        ParticipationRequest request = getRequestFromRepo(reqId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException(String.format("User with id=%d cannot approve event with id=%d",
                    user.getId(),
                    event.getInitiator().getId()));
        }
        Integer confirmedParticipationRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                ParticipationRequestState.CONFIRMED).size();
        if (confirmedParticipationRequests < event.getParticipantLimit()) {
            request.setStatus(ParticipationRequestState.CONFIRMED);
        }
        if (confirmedParticipationRequests.equals(event.getParticipantLimit())) {
            List<ParticipationRequest> requestsToCancel = requestRepository.findAllByStatus(
                    ParticipationRequestState.PENDING);
            List<ParticipationRequest> canceledRequests = new ArrayList<>();
            for (ParticipationRequest req : requestsToCancel) {
                req.setStatus(ParticipationRequestState.CANCELED);
                canceledRequests.add(req);
            }
            requestRepository.saveAll(canceledRequests);
            log.info("All unapproved requests canceled because participation limit reached.");
        }
        ParticipationRequest resultRequest = requestRepository.save(request);
        log.info("Participation request with id={} approved successfully.", resultRequest.getId());

        return mapper.toDto(resultRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long eventId, Long reqId) {
        User user = getUserFromRepo(userId);
        Event event = getEventFromRepo(eventId);
        ParticipationRequest request = getRequestFromRepo(reqId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ForbiddenException(String.format("User with id=%d cannot cancel event with id=%d",
                    user.getId(),
                    event.getInitiator().getId()));
        }
        request.setStatus(ParticipationRequestState.REJECTED);
        ParticipationRequest savedRequest = requestRepository.save(request);
        log.info("Participation request with id={} was canceled successfully.", savedRequest.getId());

        return mapper.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto getRequest(Long requestId) {
        ParticipationRequest request = getRequestFromRepo(requestId);
        log.info("Getting participation request with id={} from database.", request.getId());
        return mapper.toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsOfUser(Long userId) {
        User user = getUserFromRepo(userId);

        return requestRepository.findAllByRequester_Id(user.getId()).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsForEvent(Long eventId, Long userId) {
        User user = getUserFromRepo(userId);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, user.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("There isn't event with id=%d in repository.", eventId))
        );

        return requestRepository.findAllByEvent_Id(event.getId()).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserFromRepo(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException(
                String.format("There isn't user with id %d in this database.", id)
        ));
    }

    private ParticipationRequest getRequestFromRepo(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't participation request with id=%s in this database.", id)));
    }

    private Event getEventFromRepo(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't event with id=%d in repository.", id)
                ));
    }
}
