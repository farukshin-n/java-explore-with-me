package ru.practicum.ewmservice.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.client.StatsClient;
import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.dto.event.EventShortDto;
import ru.practicum.ewmservice.dto.compilation.NewCompilationDto;
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
import java.util.stream.Collectors;

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
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll();
        } else {
            compilations = compilationRepository.findByPinnedOrderById(pinned, pageable);
        }

        return compilations.stream()
                .map(c -> CompilationMapper.toDto(c, getEventShortDtoList(c.getEvents())))
                .collect(Collectors.toList());
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
        changePin(compilationId, true);
        log.info("Compilation with id={} pinned successfully.", compilationId);
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compilationId) {
        changePin(compilationId, false);
        log.info("Compilation with id={} unpinned successfully.", compilationId);
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

    private List<EventShortDto> getEventShortDtoList(List<Event> events) {
        List<EventShortDto> eventList = Collections.emptyList();
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = client.getStats(eventIds);
            Map<Long, Integer> eventsRequests = new HashMap<>();
            List<Integer> requestList = requestRepository.countAllByStatusAndEvent_IdsIn(
                    ParticipationRequestState.CONFIRMED, eventIds);
            for (int i = 0; i < eventIds.size(); i++) {
                eventsRequests.put(eventIds.get(i), requestList.get(i));
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

    private void changePin(Long compilationId, boolean isPinned) {
        final Compilation compilation = getCompilationFromRepo(compilationId);
        if (isPinned) {
            if (compilation.getPinned()) {
                throw new BadRequestException(
                        String.format("Compilation with id=%d is already pinned.", compilation.getId())
                );
            }
            compilation.setPinned(true);
        } else {
            if (!compilation.getPinned()) {
                throw new BadRequestException(
                        String.format("Compilation with id=%d is already not pinned.", compilation.getId())
                );
            }
            compilation.setPinned(false);
        }
        compilationRepository.save(compilation);
    }
}
