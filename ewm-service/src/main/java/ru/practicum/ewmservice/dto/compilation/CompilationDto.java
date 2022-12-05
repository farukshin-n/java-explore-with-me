package ru.practicum.ewmservice.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewmservice.dto.event.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    @NotNull
    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private Boolean pinned;
    private List<EventShortDto> events;
}
