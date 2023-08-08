package ru.practicum.explore_with_me.event.service;

import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortResponseDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullResponseDto createUserEvent(EventCreateRequestDto eventCreateRequestDto, Long userId);

    EventFullResponseDto getUserEventById(Long userId, Long eventId);

    EventFullResponseDto patchUserEvent(EventUpdateRequestDto eventUpdateRequestDto, Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResponseDto patchUserRequestStatusForEvent(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusDto);

    List<EventShortResponseDto> getPublicEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullResponseDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventFullResponseDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullResponseDto patchAdminEvent(EventUpdateRequestDto eventUpdateRequestDto, Long eventId);
}
