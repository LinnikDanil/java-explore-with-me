package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.model.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public HitDto hit(HitDto hitDto) {
        Stats stats = statsRepository.save(StatsMapper.toStats(hitDto));

        log.info("hit saved: {}", stats);
        return StatsMapper.toHitDto(stats);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<StatsDto> statsDtos;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                statsDtos = statsRepository.findStatsWithoutUrisUnique(start, end);
            } else {
                statsDtos = statsRepository.findStatsWithoutUris(start, end);
            }
        } else {
            if (unique) {
                statsDtos = statsRepository.findStatsWithUrisUnique(start, end, uris);
            } else {
                statsDtos = statsRepository.findStatsWithUris(start, end, uris);
            }
        }

        log.info("stats: {}", statsDtos);
        return statsDtos;
    }
}
