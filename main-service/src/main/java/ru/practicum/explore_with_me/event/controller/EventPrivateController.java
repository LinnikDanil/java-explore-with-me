package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.utils.EventPageConstants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortResponseDto> getShortEvents(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = EventPageConstants.PAGE_FROM) @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = EventPageConstants.PAGE_SIZE) @Positive Integer size) {
        log.info("PRIVATE CONTROLLER: GET short events. userId = {}, from = {}, size = {}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponseDto postEvent(@PathVariable Long userId,
                                          @Valid @RequestBody EventCreateRequestDto eventCreateRequestDto) {
        log.info("PRIVATE CONTROLLER: POST event: {}, id = {}", eventCreateRequestDto, userId);
        return eventService.createUserEvent(eventCreateRequestDto, userId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto getFullEventById(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        log.info("PRIVATE CONTROLLER: GET full event: userId = {}, eventId = {}", userId, eventId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullResponseDto PatchEvent(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @Valid @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("PRIVATE CONTROLLER: PATCH event: {}, userId = {}, eventId = {}", eventUpdateRequestDto, userId, eventId);
        return eventService.patchUserEvent(eventUpdateRequestDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        log.info("PRIVATE CONTROLLER: GET requests by eventId= {}, userId = {}", eventId, userId);
        return eventService.getUserRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResponseDto patchRequestsStatsForEvent(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @Valid @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusDto) {
        log.info("PRIVATE CONTROLLER: PATCH request status: {}, by eventId= {}, userId = {}", eventRequestStatusDto, eventId, userId);
        return eventService.patchUserRequestStatusForEvent(userId, eventId, eventRequestStatusDto);
    }
}
