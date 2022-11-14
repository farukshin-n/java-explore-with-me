package ru.practicum.ewmservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    @NotBlank
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
