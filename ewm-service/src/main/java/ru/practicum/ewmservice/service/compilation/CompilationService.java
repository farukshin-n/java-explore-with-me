package ru.practicum.ewmservice.service.compilation;

import ru.practicum.ewmservice.dto.compilation.CompilationDto;
import ru.practicum.ewmservice.dto.compilation.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto getCompilation(Long compilationId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto addEventToCompilation(Long eventId, Long compilationId);

    void pinCompilation(Long compilationId);

    void unpinCompilation(Long compilationId);

    void deleteCompilation(Long compilationId);

    void deleteEventFromCompilation(Long compilationId, Long eventId);
}
