package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.exception.ValidationRequestException;
import ru.practicum.stats.service.StatsService;
import ru.practicum.stats.utils.TimeFormatConstants;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.StatsDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {
    private final HitDto hitDtoTest = HitDto.builder().app("appHit").uri("uriHit").ip("Hit")
            .timestamp(LocalDateTime.now().format(TimeFormatConstants.TIMESTAMP_FORMATTER)).build();
    private final StatsDto statsDto = new StatsDto("app", "uri", 5L);
    @MockBean
    private StatsService statsService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Запись статистики")
    void hit() throws Exception {
        when(statsService.hit(Mockito.any(HitDto.class))).thenReturn(hitDtoTest);

        mvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitDtoTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.app", is(hitDtoTest.getApp())))
                .andExpect(jsonPath("$.uri", is(hitDtoTest.getUri())))
                .andExpect(jsonPath("$.ip", is(hitDtoTest.getIp())));
    }

    @Test
    @DisplayName("Получение статистики")
    void getStats() throws Exception {
        List<StatsDto> stats = List.of(statsDto);
        when(statsService.getStats(any(), any(), any(), anyBoolean())).thenReturn(stats);

        mvc.perform(get("/stats")
                        .param("start", "2023-07-20 11:39:31")
                        .param("end", "2023-07-20 21:39:31")
                        .param("unique", "false")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(stats.get(0).getApp())))
                .andExpect(jsonPath("$[0].uri", is(stats.get(0).getUri())))
                .andExpect(jsonPath("$[0].hits", is(stats.get(0).getHits()), Long.class));
    }

    @Test
    @DisplayName("Получение статистики с неправильными датами")
    void getStatsWrongDates() throws Exception {
        String start = "2023-07-21 21:39:31";
        String end = "2023-07-20 21:39:31";

        when(statsService.getStats(any(), any(), any(), anyBoolean())).thenThrow(
                new ValidationRequestException("Start can't be later than the end."));

        mvc.perform(get("/stats")
                        .param("start", start)
                        .param("end", end)
                        .param("unique", "false")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Start can't be later than the end.")));
    }
}