package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullResponseDto;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.utils.EventPageConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortResponseDto> getShortEvents(@RequestParam(required = false) String text,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(defaultValue = EventPageConstants.PAGE_FROM) @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = EventPageConstants.PAGE_SIZE) @Positive Integer size,
                                                      HttpServletRequest request) {
        log.info("PUBLIC CONTROLLER: GET short events. text: {}, categories: {}, paid = {}, rangeStart: {}, rangeEnd: {}," +
                        "onlyAvailable = {}, sort = {}, from = {}, size = {}, request = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto getFullEventById(@PathVariable Long eventId,
                                                 HttpServletRequest request) {
        log.info("PUBLIC CONTROLLER: GET full event: id = {}, request = {}", eventId, request);
        return eventService.getPublicEventById(eventId, request);
    }
}
