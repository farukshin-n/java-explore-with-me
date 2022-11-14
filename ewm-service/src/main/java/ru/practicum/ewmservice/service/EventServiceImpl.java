package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.client.StatsClient;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.BadStateException;
import ru.practicum.ewmservice.exception.DateException;
import ru.practicum.ewmservice.model.*;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.ParticipationRequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.mapper.EventMapper;
import ru.practicum.ewmservice.service.mapper.LocationMapper;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public static final String APP_NAME = "ewm-main-service";
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient client;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto eventDto, Long userId) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateException("There is less than 2 hours before event.");
        }
        final User initiator = getUserFromRepo(userId);
        final Category category = getCategoryFromRepo(eventDto.getCategory());
        final Event eventToSave = EventMapper.toEvent(initiator, category, eventDto);
        final Event savedEvent = eventRepository.save(eventToSave);
        final EventFullDto savedEventFullDto = getEventFullDto(savedEvent);
        log.info("New event with id = {} from user with id {} created successfully.",
                savedEventFullDto.getId(), userId);

        return savedEventFullDto;
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        client.sendHit(new EndPointHit(APP_NAME, uri, ip));
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("There isn't event with id %d in this database.", eventId)
        ));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event is not published.");
        }
        EventFullDto resultDto = getEventFullDto(event);
        log.info("Getting event with id={} with views = {} and confirmed requests = {}",
                resultDto.getId(), resultDto.getViews(), resultDto.getConfirmedRequests());

        return resultDto;
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        final Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                String.format("There isn't event with id=%d in repository.", eventId)
        ));
        EventFullDto resultDto = getEventFullDto(event);
        log.info("Getting event with id={} by initiator with id={}",
                resultDto.getId(), resultDto.getInitiator().getId());
        return resultDto;
    }

    @Override
    public List<EventShortDto> getAllEvents(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            int from,
                                            int size,
                                            HttpServletRequest request) {
        String statsUri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        client.sendHit(new EndPointHit(APP_NAME, statsUri, ip));
        log.info("Hit from ip={} and uri={} during getting all events was sent to statistics.",
                ip, statsUri);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        Pageable pageable = getPageable(from, size, sort);
        List<Category> categoryEntities;
        if (categories != null) {
            categoryEntities = categoryRepository.findAllByIdIn(categories);
        } else {
            categoryEntities = categoryRepository.findAll();
        }

        List<Event> events;
        List<EventShortDto> eventList = new ArrayList<>();
        boolean sortEventDate = sort.equals(EventSort.EVENT_DATE.toString()) || sort.isBlank();
        boolean sortViews = sort.equals(EventSort.VIEWS.toString());
        if (sortEventDate) {
            if (text.isBlank()) {
                events = eventRepository.getAllEventsPublicByEventDateAllText(categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            } else {
                events = eventRepository.getAllEventsPublicByEventDate(text, categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            }
            List<Event> resultEvents = getEventsAvailableOrNot(events, onlyAvailable);
            eventList = getEventShortDtoList(resultEvents);
        }

        if (sortViews) {
            if (text.isBlank()) {
                events = eventRepository.getAllEventsPublicAllText(categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            } else {
                events = eventRepository.getAllEventsPublic(text, categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            }
            List<Event> resultEvents = getEventsAvailableOrNot(events, onlyAvailable);
            eventList = getEventShortDtoList(resultEvents);
            eventList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }

        log.info("Get all events={} after search text={}.", eventList, text);

        return eventList;
    }

    @Override
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users,
                                                   List<String> states,
                                                   List<Long> categories,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   int from,
                                                   int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        final PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Category> categoryEntities;
        if (categories != null && !categories.isEmpty()) {
            categoryEntities = categoryRepository.findAllByIdIn(categories);
        } else {
            categoryEntities = categoryRepository.findAll();
        }
        List<User> userEntities;
        if (users != null && !users.isEmpty()) {
            userEntities = userRepository.findAllByIdIn(users);
        } else {
            userEntities = userRepository.findAll();
        }
        List<EventState> statesEnum = new ArrayList<>();
        if (states != null) {
            for (String state: states) {
                EventState status = EventState.valueOf(state);
                statesEnum.add(status);
            }
        } else {
            statesEnum.addAll(Arrays.asList(EventState.values()));
        }

        List<Event> events = eventRepository.getAllEventsByAdmin(userEntities, statesEnum,
                categoryEntities, rangeStart, rangeEnd, pageRequest);
        List<EventFullDto> eventList = getEventFullDtoList(events);
        log.info("Get all events list with length={} by admin request.", eventList.size());

        return eventList;
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        User initiator = getUserFromRepo(userId);
        List<Event> eventList = eventRepository.findEventsByInitiator_IdOrderById(
                initiator.getId(), PageRequest.of(from / size, size));
        List<EventShortDto> result = getEventShortDtoList(eventList);
        log.info("Get all events by request of user with id={}.", initiator.getId());

        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, UpdateEventRequest eventRequest) {
        final User initiator = getUserFromRepo(userId);
        final Event event = getEventFromRepo(eventRequest.getEventId());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(String.format("Event with status %s cannot be edit.",
                    EventState.PUBLISHED));
        }
        if (eventRequest.getEventDate() != null) {
            if (eventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("There is less than 2 hours before event.");
            }
            event.setEventDate(eventRequest.getEventDate());
        }
        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }
        if (eventRequest.getAnnotation() != null) {
            event.setAnnotation(eventRequest.getAnnotation());
        }
        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }
        if (eventRequest.getCategory() != null) {
            Category category = getCategoryFromRepo(eventRequest.getCategory());
            event.setCategory(category);
        }
        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }
        if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }
        Event savedEvent = eventRepository.save(event);
        log.info("Event with id={} updated successfully by user with id={}.", savedEvent.getId(), initiator.getId());

        return getEventFullDto(savedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
        log.info("Event with id {} deleted successfully", eventId);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event updatingEvent = getEventFromRepo(eventId);
        if (adminUpdateEventRequest.getTitle() != null) {
            updatingEvent.setTitle(adminUpdateEventRequest.getTitle());
        }
        if (adminUpdateEventRequest.getAnnotation() != null) {
            updatingEvent.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }
        if (adminUpdateEventRequest.getDescription() != null) {
            updatingEvent.setDescription(adminUpdateEventRequest.getDescription());
        }
        if (adminUpdateEventRequest.getCategory() != null) {
            Category updatedCategory = getCategoryFromRepo(adminUpdateEventRequest.getCategory());
            updatingEvent.setCategory(updatedCategory);
        }
        if (adminUpdateEventRequest.getPaid() != null) {
            updatingEvent.setPaid(adminUpdateEventRequest.getPaid());
        }
        if (adminUpdateEventRequest.getLocation() != null) {
            updatingEvent.setLocation(LocationMapper.toLocation(adminUpdateEventRequest.getLocation()));
        }
        if (adminUpdateEventRequest.getEventDate() != null) {
            updatingEvent.setEventDate(adminUpdateEventRequest.getEventDate());
        }
        if (adminUpdateEventRequest.getRequestModeration() != null) {
            updatingEvent.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }
        if (adminUpdateEventRequest.getParticipantLimit() != null) {
            updatingEvent.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        }
        Event updatedEvent = eventRepository.save(updatingEvent);
        log.info("Admin updated event with id={} successfully.", updatedEvent.getId());

        return getEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto cancelEventByInitiator(Long userId, Long eventId) {
        User initiator = getUserFromRepo(userId);
        Event event = getEventFromRepo(eventId);
        validateInitiator(initiator.getId(), event.getInitiator().getId(), event.getId());
        validateState(event, "cancel");
        event.setState(EventState.CANCELED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event with id={} canceled by initiator (id={}).", event.getId(), initiator.getId());

        return getEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto cancelEventByAdmin(Long eventId) {
        Event event = getEventFromRepo(eventId);
        validateState(event, "cancel by admin");
        event.setState(EventState.CANCELED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event with id={} canceled by administrator.", event.getId());

        return getEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        Event publishingEvent = getEventFromRepo(eventId);
        validateState(publishingEvent, "publish");
        if (publishingEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new DateException("There is less than 1 hour before event.");
        }
        publishingEvent.setState(EventState.PUBLISHED);
        publishingEvent.setPublishedOn(LocalDateTime.now());
        Event publishedEvent = eventRepository.save(publishingEvent);
        log.info("Event with id={} published successfully at {}",
                publishedEvent.getId(), publishedEvent.getPublishedOn());

        return getEventFullDto(publishedEvent);
    }

    private User getUserFromRepo(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException(
                String.format("There isn't user with id %d in this database.", userId)
        ));
    }

    private Category getCategoryFromRepo(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't category with id=%d in repository.", categoryId)
                ));
    }

    private Event getEventFromRepo(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't event with id=%d in repository.", eventId)
                ));
    }

    private void validateInitiator(Long userId, Long eventInitiatorId, Long eventId) {
        if (!userId.equals(eventInitiatorId)) {
            throw new ValidationException(
                    String.format("User with id=%d is not the initiator of event with id=%d",
                            userId,
                            eventId)
            );
        }
    }

    private void validateState(Event event, String action) {
        if (!event.getState().equals(EventState.PENDING)) {
            throw new BadStateException(String.format(
                    "You can't %s event with status %s.", action, event.getState()
            ));
        }
    }

    private Pageable getPageable(Integer from, Integer size, String sort) {
        switch (EventSort.valueOf(sort)) {
            case EVENT_DATE:
                sort = "eventDate";
                return PageRequest.of(from, size, Sort.by(sort).ascending());
            case VIEWS:
                return PageRequest.of(from, size, Sort.by(sort.toLowerCase()).ascending());
            default:
                throw new BadStateException("There isn't such way of sort.");
        }
    }

    private EventFullDto getEventFullDto(Event event) {
        Map<Long, Long> views = client.getStats(Collections.singletonList(event.getId()));
        Integer confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                ParticipationRequestState.CONFIRMED).size();
        Map<Long, Integer> confirmedRequestsForDto = new HashMap<>(Map.of(event.getId(), confirmedRequests));

        return EventMapper.toEventFullDto(event, confirmedRequestsForDto, views);
    }

    private List<EventShortDto> getEventShortDtoList(Iterable<Event> events) {
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = client.getStats(eventIds);
            Map<Long, Integer> eventsRequests = new HashMap<>();
            for (Long id : eventIds) {
                int confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(id,
                        ParticipationRequestState.CONFIRMED).size();
                eventsRequests.put(id, confirmedRequests);
            }
            return EventMapper.toEventShortDtoList(events, eventsRequests, views);
        }
        return Collections.emptyList();
    }

    private List<EventFullDto> getEventFullDtoList(Iterable<Event> events) {
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = client.getStats(eventIds);
            Map<Long, Integer> eventsRequests = new HashMap<>();
            for (Long id : eventIds) {
                int confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(id,
                        ParticipationRequestState.CONFIRMED).size();
                eventsRequests.put(id, confirmedRequests);
            }
            return EventMapper.toEventFullDtoList(events, eventsRequests, views);
        }
        return Collections.emptyList();
    }

    private List<Event> getEventsAvailableOrNot(List<Event> events, boolean onlyAvailable) {
        List<Event> resultEvents = new ArrayList<>();
        if (onlyAvailable) {
            List<Long> eventIds = getEventIds(events);
            for (int i = 0; i < eventIds.size(); i++) {
                if (events.get(i).isRequestModeration()) {
                    int confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(events.get(i).getId(),
                            ParticipationRequestState.CONFIRMED).size();
                    if ((events.get(i).getParticipantLimit() - confirmedRequests) > 0) {
                        resultEvents.add(events.get(i));
                    }
                }
            }
            return resultEvents;
        }
        return events;
    }

    private List<Long> getEventIds(Iterable<Event> events) {
        List<Long> result = new ArrayList<>();
        events.forEach(e -> result.add(e.getId()));
        return result;
    }
}
