package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.CompilationDto;
import ru.practicum.ewmservice.dto.NewCompilationDto;

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
