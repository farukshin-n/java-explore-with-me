package ru.practicum.ewmservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.LocationDto;
import ru.practicum.ewmservice.model.Location;

@Component
public class LocationMapper {
    public static Location toLocation(LocationDto dto) {
        return new Location(
                dto.getLat(),
                dto.getLon()
        );
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
