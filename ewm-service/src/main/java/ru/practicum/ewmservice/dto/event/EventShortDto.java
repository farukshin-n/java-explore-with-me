package ru.practicum.ewmservice.dto.event;

import lombok.*;
import ru.practicum.ewmservice.dto.category.CategoryDto;
import ru.practicum.ewmservice.dto.user.UserShortDto;

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
