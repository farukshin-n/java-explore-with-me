package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsserver.dto.EndPointHit;
import ru.practicum.statsserver.dto.ViewStats;
import ru.practicum.statsserver.service.HitService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService statsService;

    @PostMapping(path = "/hit")
    public void postHit(@RequestBody EndPointHit hitDto) {
        log.info("Received Post-request with parameters: app – {}, uri={}, ip={}",
                hitDto.getApp(), hitDto.getUri(), hitDto.getIp());
        statsService.postHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Received Get-request for statistics from {} to {} with uris {}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
