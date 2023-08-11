package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.utils.TimeFormatConstants;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsServiceIntegrationTest {
    @Autowired
    private final StatsService statsService;

    @Test
    @DisplayName("Full integration test of the statistics service and repository")
    void hitAndGetStats() {
        //test hitDto
        HitDto hitDtoTest1 = HitDto.builder().app("appHit1").uri("uriHit1").ip("1Hit")
                .timestamp(LocalDateTime.now().format(TimeFormatConstants.TIMESTAMP_FORMATTER)).build();
        HitDto hitDtoTest2 = HitDto.builder().app("appHit2").uri("uriHit2").ip("1Hit")
                .timestamp(LocalDateTime.now().format(TimeFormatConstants.TIMESTAMP_FORMATTER)).build();
        HitDto hitDtoTest3 = HitDto.builder().app("appHit3").uri("uriHit1").ip("2Hit")
                .timestamp(LocalDateTime.now().format(TimeFormatConstants.TIMESTAMP_FORMATTER)).build();
        HitDto hitDtoTest4 = HitDto.builder().app("appHit1").uri("uriHit1").ip("1Hit")
                .timestamp(LocalDateTime.now().format(TimeFormatConstants.TIMESTAMP_FORMATTER)).build();

        HitDto hitDto1 = statsService.hit(hitDtoTest1);
        HitDto hitDto2 = statsService.hit(hitDtoTest2);
        HitDto hitDto3 = statsService.hit(hitDtoTest3);
        HitDto hitDto4 = statsService.hit(hitDtoTest4);

        assertNotNull(hitDto1);
        assertNotNull(hitDto2);
        assertNotNull(hitDto3);
        assertNotNull(hitDto4);
        assertEquals(hitDto1.getApp(), hitDtoTest1.getApp());
        assertEquals(hitDto1.getIp(), hitDtoTest1.getIp());
        assertEquals(hitDto1.getUri(), hitDtoTest1.getUri());
        assertEquals(hitDto1.getTimestamp(), hitDtoTest1.getTimestamp());

        //findStatsWithoutUris
        List<StatsDto> listStatsDto =
                statsService.getStats(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), null, false);
        assertNotNull(listStatsDto);
        assertEquals(3, listStatsDto.size());

        StatsDto statsDto1 = listStatsDto.stream().findFirst().orElse(null);
        assertEquals(statsDto1.getApp(), hitDtoTest1.getApp());
        assertEquals(statsDto1.getUri(), hitDtoTest1.getUri());
        assertEquals(statsDto1.getHits(), 2);

        //findStatsWithoutUrisUnique
        List<StatsDto> listStatsDto2 =
                statsService.getStats(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), null, true);
        assertNotNull(listStatsDto2);
        assertEquals(3, listStatsDto2.size());

        //findStatsWithUris
        List<StatsDto> listStatsDto3 =
                statsService.getStats(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                        List.of("uriHit1"), false);
        assertNotNull(listStatsDto3);
        assertEquals(2, listStatsDto3.size());

        StatsDto statsDto3 = listStatsDto3.stream().skip(1).findFirst().orElse(null);
        assertEquals(statsDto3.getApp(), hitDtoTest3.getApp());
        assertEquals(statsDto3.getUri(), hitDtoTest3.getUri());
        assertEquals(statsDto3.getHits(), 1);

        //findStatsWithUrisUnique
        List<StatsDto> listStatsDto4 =
                statsService.getStats(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1),
                        List.of("uriHit1"), true);
        assertNotNull(listStatsDto4);
        assertEquals(2, listStatsDto4.size());
    }
}