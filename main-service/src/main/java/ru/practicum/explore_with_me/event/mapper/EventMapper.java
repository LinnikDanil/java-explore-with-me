package ru.practicum.explore_with_me.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.event.dto.EventCreateRequestDto;
import ru.practicum.explore_with_me.event.dto.EventFullResponseDto;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.location.mapper.LocationMapper;
import ru.practicum.explore_with_me.location.model.Location;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.utils.EventTimeFormatConstants.TIMESTAMP_FORMATTER;

@UtilityClass
public class EventMapper {
    public static EventShortResponseDto toEventShortDto(Event event) {
        return EventShortResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(TIMESTAMP_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static EventShortResponseDto toEventShortDto(Event event, long confirmedRequests, long views) {
        return EventShortResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate().format(TIMESTAMP_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static Event toEvent(EventCreateRequestDto eventDto, Category category, User initiator, Location location,
                                LocalDateTime createdOn, EventState eventState) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .category(category)
                .description(eventDto.getDescription())
                .createdOn(createdOn)
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), TIMESTAMP_FORMATTER))
                .initiator(initiator)
                .location(location)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(eventState)
                .build();
    }

    public static EventFullResponseDto toEventFullDto(Event event) {
        return EventFullResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(0L)
                .createdOn(event.getCreatedOn().format(TIMESTAMP_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(TIMESTAMP_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(0L)
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(TIMESTAMP_FORMATTER) : null)
                .build();
    }

    public static EventFullResponseDto toEventFullDto(Event event, long confirmedRequests, long views) {
        return EventFullResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn().format(TIMESTAMP_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(TIMESTAMP_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views)
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(TIMESTAMP_FORMATTER) : null)
                .build();
    }
}
