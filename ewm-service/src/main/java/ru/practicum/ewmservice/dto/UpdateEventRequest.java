package ru.practicum.ewmservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class UpdateEventRequest {
    @NotNull
    private Long eventId;
    private String title;
    private String annotation;
    private String description;
    private Long category;
    private Boolean paid;
    private LocalDateTime eventDate;
    private Integer participantLimit;
}
