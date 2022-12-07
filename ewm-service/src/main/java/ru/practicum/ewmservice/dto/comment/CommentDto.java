package ru.practicum.ewmservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String comment;
    private Long author;
    private Long event;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Boolean visible;
}
