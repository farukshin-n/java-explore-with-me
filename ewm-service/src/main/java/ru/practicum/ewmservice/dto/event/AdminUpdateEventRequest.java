package ru.practicum.ewmservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewmservice.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminUpdateEventRequest {
    @NotBlank
    @Size(message = "Title's length must be between 3 and 120 characters", max = 120, min = 3)
    private String title;
    @NotBlank @Size(message = "Annotation's length must be between 20 and 2000 characters", max = 2000, min = 20)
    private String annotation;
    @NotBlank @Size(message = "Description's length must be between 20 and 7000 characters", max = 7000, min = 20)
    private String description;
    private Long category;
    private Boolean paid;
    private LocationDto location;
    @Future
    private LocalDateTime eventDate;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
}
