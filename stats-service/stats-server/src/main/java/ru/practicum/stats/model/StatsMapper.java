package ru.practicum.stats.model;

import lombok.experimental.UtilityClass;
import ru.practicum.stats_dto.HitDto;

import java.time.LocalDateTime;

import static ru.practicum.stats.utils.TimeFormatUtil.TIMESTAMP_FORMATTER;

@UtilityClass
public class StatsMapper {

    public static Stats toStats(HitDto hitDto) {
        return Stats.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), TIMESTAMP_FORMATTER))
                .build();
    }

    public static HitDto toHitDto(Stats stats) {
        return HitDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .ip(stats.getIp())
                .timestamp(stats.getTimestamp().format(TIMESTAMP_FORMATTER))
                .build();
    }
}
