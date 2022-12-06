package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsserver.repository.HitRepository;
import ru.practicum.statsserver.dto.EndPointHit;
import ru.practicum.statsserver.model.Hit;
import ru.practicum.statsserver.dto.ViewStats;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public void postHit(EndPointHit hitDto) {
        final Hit hit = hitRepository.save(HitMapper.toHit(hitDto));
        log.info("Hit with id={} successfully saved.", hit.getId());
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime startTime = LocalDateTime.parse(decodedStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(decodedEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }
        List<ViewStats> statsList;
        if (unique) {
            log.info("Unique={}, getting all unique stats", unique);
            statsList = hitRepository.getUniqueStats(startTime, endTime, uris);
        } else {
            log.info("Getting all stats");
            statsList = hitRepository.getAllStats(startTime, endTime, uris);
        }
        return statsList;
        /*
        LocalDateTime startFormatted = LocalDateTime.parse(start, dateFormat);
        LocalDateTime endFormatted = LocalDateTime.parse(end, dateFormat);

        List<Object[]> endpointHits = new ArrayList<>();
        if (unique.equals(true)) {
            endpointHits = hitRepository.getUniqueEndpointHits(startFormatted, endFormatted, uris);
        } else {
            endpointHits = hitRepository.getEndpointHits(startFormatted, endFormatted, uris);
        }
        List<ViewStats> viewStats = new ArrayList<>();

        if (!endpointHits.isEmpty()) {
            for (Object[] object : endpointHits) {
                ViewStats viewStatsDto = new ViewStats();
                viewStatsDto.setApp(object[0].toString());
                viewStatsDto.setUri(object[1].toString());
                viewStatsDto.setHits(Long.valueOf(object[2].toString()));
                viewStats.add(viewStatsDto);
            }
        }
        return viewStats;
         */
    }
}
