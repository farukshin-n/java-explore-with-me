package ru.practicum.ewmservice.dto.event;

import lombok.*;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.model.EventState;
import ru.practicum.ewmservice.model.Location;
import ru.practicum.ewmservice.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private Category category;
    private boolean paid;
    private User initiator;
    private Location location;
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Integer participantLimit;
    private EventState state;
    private boolean requestModeration;
    private Integer confirmedRequests;
    private Long views;
}
