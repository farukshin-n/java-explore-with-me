package ru.practicum.ewmservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.dto.event.EventShortDto;
import ru.practicum.ewmservice.dto.compilation.NewCompilationDto;
import ru.practicum.ewmservice.model.Compilation;
import ru.practicum.ewmservice.model.Event;

import java.util.List;

@Component
public class CompilationMapper {
    public static CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                events
        );
    }

    public static Compilation toCompilation(NewCompilationDto dto, List<Event> events) {
        return new Compilation(
                null,
                dto.getTitle(),
                dto.getPinned(),
                events
        );
    }

    public static Compilation toCompilation(CompilationDto dto, List<Event> events) {
        return new Compilation(
                dto.getId(),
                dto.getTitle(),
                dto.getPinned(),
                events
        );
    }
}
