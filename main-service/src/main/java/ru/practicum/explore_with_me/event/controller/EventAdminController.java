package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.EventFullResponseDto;
import ru.practicum.explore_with_me.event.dto.EventUpdateRequestDto;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.utils.EventPageConstants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullResponseDto> getFullEvents(@RequestParam(required = false) List<Long> users,
                                                    @RequestParam(required = false) List<String> states,
                                                    @RequestParam(required = false) List<Long> categories,
                                                    @RequestParam(required = false) String rangeStart,
                                                    @RequestParam(required = false) String rangeEnd,
                                                    @RequestParam(defaultValue = EventPageConstants.PAGE_FROM)
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = EventPageConstants.PAGE_SIZE)
                                                    @Positive Integer size) {
        log.info(
                "ADMIN CONTROLLER: GET full events. Users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto patchEvent(@PathVariable Long eventId,
                                           @Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("ADMIN CONTROLLER: PATCH event: {}, id = {}", eventUpdateRequestDto, eventId);
        return eventService.patchAdminEvent(eventUpdateRequestDto, eventId);
    }
}
