package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.exception.ValidationRequestException;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.utils.TimeFormatUtil;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.StatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto hit(@Valid @RequestBody HitDto hitDto) {
        log.info("hit stats {}", hitDto);
        return statsService.hit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam @DateTimeFormat(pattern = TimeFormatUtil.TIMESTAMP_FORMAT) LocalDateTime start,
                                   @RequestParam @DateTimeFormat(pattern = TimeFormatUtil.TIMESTAMP_FORMAT) LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique
    ) {
        if (start.isAfter(end)) {
            throw new ValidationRequestException("Start can't be later than the end.");
        }

        log.info("get stats start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
