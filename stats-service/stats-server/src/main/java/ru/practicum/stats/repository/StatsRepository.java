package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT NEW ru.practicum.stats_dto.StatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
        "FROM Stats AS s " +
        "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
        "GROUP BY s.app, s.uri " +
        "ORDER BY COUNT(DISTINCT s.ip ) DESC")
    List<StatsDto> findStatsWithoutUrisUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.stats_dto.StatsDto(s.app, s.uri, COUNT(s.ip)) " +
        "FROM Stats AS s " +
        "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
        "GROUP BY s.app, s.uri " +
        "ORDER BY COUNT(s.ip ) DESC")
    List<StatsDto> findStatsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT NEW ru.practicum.stats_dto.StatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
        "FROM Stats AS s " +
        "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
        "AND s.uri IN (?3) " +
        "GROUP BY s.app, s.uri " +
        "ORDER BY COUNT(DISTINCT s.ip ) DESC")
    List<StatsDto> findStatsWithUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT NEW ru.practicum.stats_dto.StatsDto(s.app, s.uri, COUNT(s.ip)) " +
        "FROM Stats AS s " +
        "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
        "AND s.uri IN (?3) " +
        "GROUP BY s.app, s.uri " +
        "ORDER BY COUNT(s.ip ) DESC")
    List<StatsDto> findStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
