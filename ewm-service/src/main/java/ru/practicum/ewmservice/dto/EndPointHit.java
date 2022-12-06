package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class EndPointHit {
    private Long id;
    @NotBlank
    private final String app;
    @NotBlank
    private final String uri;
    @NotBlank
    private final String ip;
    private LocalDateTime timestamp = LocalDateTime.now();
}
