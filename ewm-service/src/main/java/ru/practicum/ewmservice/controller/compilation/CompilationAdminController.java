package ru.practicum.ewmservice.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.CompilationDto;
import ru.practicum.ewmservice.dto.NewCompilationDto;
import ru.practicum.ewmservice.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Get request for adding compilation with title {}", newCompilationDto.getTitle());
        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public CompilationDto addEventToCompilation(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long compId) {
        log.info("Get request for adding event with id={} to compilation with id={}.", eventId, compId);
        return compilationService.addEventToCompilation(eventId, compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable @Positive Long compId) {
        log.info("Get request for pinning compilation with id={}.", compId);
        compilationService.pinCompilation(compId);
    }

    @DeleteMapping("{compId}")
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("Get request for deleting compilation with id={}.", compId);
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable @Positive Long compId,
                                           @PathVariable @Positive Long eventId) {
        log.info("Get request for deleting event with id={} from compilation with id={}.", compId, eventId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpinCompilation(@PathVariable @Positive Long compId) {
        log.info("Get request for pinning compilation with id={}.", compId);
        compilationService.unpinCompilation(compId);
    }
}
