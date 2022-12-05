package ru.practicum.ewmservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.event.EventFullDto;
import ru.practicum.ewmservice.dto.event.EventShortDto;
import ru.practicum.ewmservice.dto.event.NewEventDto;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.EventState;
import ru.practicum.ewmservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventMapper {
    public static Event toEvent(User initiator, Category category, NewEventDto newEventDto) {
        return new Event(
            null,
                newEventDto.getTitle(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                category,
                newEventDto.isPaid(),
                initiator,
                newEventDto.getLocation(),
                newEventDto.getEventDate(),
                LocalDateTime.now(),
                null,
                newEventDto.getParticipantLimit(),
                EventState.PENDING,
                newEventDto.isRequestModeration()
        );
    }

    public static List<EventFullDto> toEventFullDtoList(List<Event> events,
                                                  Map<Long, Integer> confirmedRequests,
                                                  Map<Long, Long> views) {
        return events.stream()
                .map(e -> toEventFullDto(e, confirmedRequests, views))
                .collect(Collectors.toList());
    }

    public static EventFullDto toEventFullDto(Event event,
                                              Map<Long, Integer> confirmedRequests,
                                              Map<Long, Long> views) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getDescription(),
                event.getCategory(),
                event.isPaid(),
                event.getInitiator(),
                event.getLocation(),
                event.getEventDate(),
                event.getCreatedOn(),
                event.getPublishedOn(),
                event.getParticipantLimit(),
                event.getState(),
                event.isRequestModeration(),
                Optional.ofNullable(confirmedRequests.get(event.getId())).orElse(0),
                Optional.ofNullable(views.get(event.getId())).orElse(0L)
        );
    }

    public static List<EventShortDto> toEventShortDtoList(List<Event> events,
                                                       Map<Long, Integer> confirmedRequests,
                                                       Map<Long, Long> views) {
        return events.stream()
                .map(e -> toEventShortDto(e, confirmedRequests, views))
                .collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event,
                                                Map<Long, Integer> confirmedRequests,
                                                Map<Long, Long> views) {
        return new EventShortDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.isPaid(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getEventDate(),
                Optional.ofNullable(confirmedRequests.get(event.getId())).orElse(0),
                Optional.ofNullable(views.get(event.getId())).orElse(0L)
        );
    }
}
