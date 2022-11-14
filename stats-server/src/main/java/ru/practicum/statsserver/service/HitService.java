package ru.practicum.statsserver.service;

import ru.practicum.statsserver.dto.EndPointHit;
import ru.practicum.statsserver.dto.ViewStats;

import java.util.List;

public interface HitService {
    void postHit(EndPointHit hitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);

}
