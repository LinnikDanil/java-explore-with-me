package ru.practicum.explore_with_me.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.RequestStatuses;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.utils.EventTimeFormatConstants.TIMESTAMP_FORMATTER;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(LocalDateTime created, Event event, User requester, RequestStatuses status) {
        return Request.builder()
                .created(created)
                .event(event)
                .requester(requester)
                .status(status)
                .build();
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated().format(TIMESTAMP_FORMATTER))
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }
}
