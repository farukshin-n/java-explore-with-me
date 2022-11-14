package ru.practicum.ewmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminUpdateEventRequest {
    private String title;
    private String annotation;
    private String description;
    private Long category;
    private Boolean paid;
    private LocationDto location;
    private LocalDateTime eventDate;
    private Integer participantLimit;
    private Boolean requestModeration;
}
