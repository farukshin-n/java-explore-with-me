package ru.practicum.statsserver.service;

import ru.practicum.statsserver.dto.EndPointHit;
import ru.practicum.statsserver.model.Hit;

public class HitMapper {
    public static Hit toHit(EndPointHit dto) {
        return new Hit(
                null,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }
}
