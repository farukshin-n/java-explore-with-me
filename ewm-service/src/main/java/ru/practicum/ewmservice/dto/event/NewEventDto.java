package ru.practicum.ewmservice.dto.event;

import lombok.*;
import ru.practicum.ewmservice.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class NewEventDto {
    @NotBlank @Size(message = "Title's length must be between 3 and 120 characters", max = 120, min = 3)
    private String title;
    @NotBlank @Size(message = "Annotation's length must be between 20 and 2000 characters", max = 2000, min = 20)
    private String annotation;
    @NotBlank @Size(message = "Description's length must be between 20 and 7000 characters", max = 7000, min = 20)
    private String description;
    @NotNull
    private Long category;
    private boolean paid;
    @NotNull
    private Location location;
    @NotNull
    @Future
    private LocalDateTime eventDate;
    @PositiveOrZero
    private Integer participantLimit;
    private boolean requestModeration;
}
