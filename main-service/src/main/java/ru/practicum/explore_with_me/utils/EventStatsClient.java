package ru.practicum.explore_with_me.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.error.exception.GettingStatisticsEwmException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.stats_client.StatsClient;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.EventTimeFormatConstants.TIMESTAMP_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStatsClient {
    private final StatsClient statsClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.name}")
    private String appName;

    public void addHit(HttpServletRequest request) {
        log.info("EVENT CLIENT: Send request: uri = {}, ip = {}", request.getRequestURI(), request.getRemoteAddr());
        statsClient.hit(new HitDto(appName, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now().format(TIMESTAMP_FORMATTER)));
    }

    public Map<Long, Long> getViewsPerEvent(List<Event> events) {
        List<String> eventUris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        log.info("EVENT CLIENT: Get stats by events: {}", events);

        List<StatsDto> stats = getStatsDtoList(eventUris);
        return stats.stream()
                .filter(s -> s.getUri().startsWith("/events/"))
                .collect(Collectors.groupingBy(s -> Long.valueOf(s.getUri().substring("/events/".length())), Collectors.counting()));
    }

    public long getViews(long eventId) {
        List<StatsDto> stats = getStatsDtoList(List.of("/events/" + eventId));
        log.info("EVENT CLIENT: Get stats: {}", stats);
        return stats.stream().findFirst().map(StatsDto::getHits).orElse(0L);
    }

    private List<StatsDto> getStatsDtoList(List<String> uriList) {
        ResponseEntity<Object> responseEntity = statsClient.getStats(
                LocalDateTime.now().minusYears(100),
                LocalDateTime.now(),
                uriList,
                true);

        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                return responseEntity.hasBody() ? Arrays.asList(mapper.readValue(mapper.writeValueAsString(responseEntity.getBody()), StatsDto[].class)) : Collections.emptyList();
            } catch (IOException exception) {
                throw new ClassCastException(exception.getMessage());
            }
        } else {
            throw new GettingStatisticsEwmException("There was a problem when getting statistics for an event");
        }
    }
}