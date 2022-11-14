package ru.practicum.ewmservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private boolean paid;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private Integer confirmedRequests;
    private Long views;
}
