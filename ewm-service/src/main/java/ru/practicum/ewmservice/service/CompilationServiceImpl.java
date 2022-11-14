package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.client.StatsClient;
import ru.practicum.ewmservice.dto.CompilationDto;
import ru.practicum.ewmservice.dto.EventShortDto;
import ru.practicum.ewmservice.dto.NewCompilationDto;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.Compilation;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.ParticipationRequestState;
import ru.practicum.ewmservice.repository.CompilationRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.ParticipationRequestRepository;
import ru.practicum.ewmservice.service.mapper.CompilationMapper;
import ru.practicum.ewmservice.service.mapper.EventMapper;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient client;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        final List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        final Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        final Compilation newCompilation = compilationRepository.save(compilation);
        log.info("New compilation with id={} created successfully.", newCompilation.getId());

        return CompilationMapper.toDto(newCompilation, getEventShortDtoList(events));
    }

    @Override
    public CompilationDto getCompilation(Long compilationId) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        List<Event> events = compilation.getEvents();

        return CompilationMapper.toDto(compilation, getEventShortDtoList(events));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<CompilationDto> result = new ArrayList<>();
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll();
        } else {
            compilations = compilationRepository.findByPinnedOrderById(pinned, pageable);
        }
        for (Compilation compilation : compilations) {
            List<EventShortDto> events = getEventShortDtoList(compilation.getEvents());
            result.add(CompilationMapper.toDto(compilation, events));
        }
        return result;
    }

    @Override
    @Transactional
    public CompilationDto addEventToCompilation(Long eventId, Long compilationId) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        final Event event = getEventFromRepo(eventId);
        if (compilation.getEvents().contains(event)) {
            throw new BadRequestException(
                    String.format("Event with id=%d already existed in compilation with id=%d",
                            event.getId(),
                            compilation.getId())
            );
        }
        List<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        Compilation resultCompilation = compilationRepository.save(compilation);
        log.info("Event with id={} successfully added to compilation with id={}.", event.getId(), compilation.getId());

        return CompilationMapper.toDto(resultCompilation, getEventShortDtoList(events));
    }

    @Override
    @Transactional
    public void pinCompilation(Long compilationId) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        if (compilation.getPinned()) {
            throw new BadRequestException(
                    String.format("Compilation with id=%d is already pinned.", compilation.getId())
            );
        }
        compilation.setPinned(true);
        final Compilation resultCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id={} pinned successfully.", resultCompilation.getId());
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compilationId) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        if (!compilation.getPinned()) {
            throw new BadRequestException(
                    String.format("Compilation with id=%d is already not pinned.", compilation.getId())
            );
        }
        compilation.setPinned(false);
        final Compilation resultCompilation = compilationRepository.save(compilation);
        log.info("Compilation with id={} pinned successfully.", resultCompilation.getId());
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        compilationRepository.deleteById(compilationId);
        log.info("Compilation with id={} deleted successfully.", compilationId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compilationId, Long eventId) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        final Event event = getEventFromRepo(eventId);
        if (!compilation.getEvents().contains(event)) {
            throw new BadRequestException(
                    String.format("Event with id=%d is already not exist in compilation with id=%d",
                            event.getId(),
                            compilation.getId())
            );
        }
        List<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        Compilation resultCompilation = compilationRepository.save(compilation);
        log.info("Event with id={} successfully deleted from compilation with id={}.",
                event.getId(), resultCompilation.getId());
    }

    private Compilation getCompilationFromRepo(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't compilation with id %d in this database.", compilationId)
                ));
    }

    private List<EventShortDto> getEventShortDtoList(Iterable<Event> events) {
        List<EventShortDto> eventList = Collections.emptyList();
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = client.getStats(eventIds);
            Map<Long, Integer> eventsRequests = new HashMap<>();
            for (Long eventId : eventIds) {
                int confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(eventId,
                        ParticipationRequestState.CONFIRMED).size();
                eventsRequests.put(eventId, confirmedRequests);
            }
            eventList = EventMapper.toEventShortDtoList(events, eventsRequests, views);
        }
        return eventList;
    }

    private List<Long> getEventIds(Iterable<Event> events) {
        List<Long> result = new ArrayList<>();
        events.forEach(e -> result.add(e.getId()));
        return result;
    }

    private Event getEventFromRepo(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't event with id=%d in repository.", eventId)
                ));
    }
}
