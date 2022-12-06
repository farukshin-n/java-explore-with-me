package ru.practicum.statsserver.dto;

import lombok.Value;

@Value
public class ViewStats {
    String app;
    String uri;
    Long hits;
}
