package ru.practicum.explore_with_me.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.location.dto.LocationDto;
import ru.practicum.explore_with_me.location.model.Location;

@UtilityClass
public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
