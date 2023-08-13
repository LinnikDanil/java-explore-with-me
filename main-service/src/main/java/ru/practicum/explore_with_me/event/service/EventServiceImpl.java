package ru.practicum.explore_with_me.event.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.comment.dto.CommentShortResponseDto;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.CommentState;
import ru.practicum.explore_with_me.comment.repository.CommentRepository;
import ru.practicum.explore_with_me.error.exception.ForbiddenActionEwmException;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.error.exception.ValidationEwmException;
import ru.practicum.explore_with_me.event.dto.*;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.QEvent;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.model.enums.StateActionForUser;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.location.mapper.LocationMapper;
import ru.practicum.explore_with_me.location.model.Location;
import ru.practicum.explore_with_me.location.repository.LocationRepository;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.RequestStatuses;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;
import ru.practicum.explore_with_me.utils.EventStatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.EventTimeFormatConstants.TIMESTAMP_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventStatsClient eventStatsClient;
    private final CommentRepository commentRepository;

    @Override
    public List<EventShortResponseDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("EVENT SERVICE: GET events userId = {}, from = {}, size = {}", userId, from, size);

        getUserOrThrow(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullResponseDto createUserEvent(EventCreateRequestDto eventRequestDto, Long userId) {
        log.info("EVENT SERVICE: CREATE event: {}, userId = {}", eventRequestDto, userId);

        checkTimeEvent(eventRequestDto.getEventDate());
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(eventRequestDto.getCategory());
        Location location = getOrSaveLocation(LocationMapper.toLocation(eventRequestDto.getLocation()));

        Event event =
                EventMapper.toEvent(eventRequestDto, category, user, location, LocalDateTime.now(), EventState.PENDING);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullResponseDto getUserEventById(Long userId, Long eventId) {
        log.info("EVENT SERVICE: GET event user id = {}, event id = {}", userId, eventId);

        Event event = getEventByEventIdAndInitiatorIdOrThrow(eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullResponseDto patchUserEvent(EventUpdateRequestDto eventDto, Long userId, Long eventId) {
        log.info("EVENT SERVICE: PATCH event: {}, user id = {}, event id = {}", eventDto, userId, eventId);

        checkTimeEvent(eventDto.getEventDate());
        Event event = getEventByEventIdAndInitiatorIdOrThrow(eventId, userId);

        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenActionEwmException("Only canceled events or events pending moderation can be modified");
        }
        if (eventDto.getStateAction() != null &&
                StateActionForUser.CANCEL_REVIEW.equals(StateActionForUser.valueOf(eventDto.getStateAction()))) {
            event.setState(EventState.CANCELED);
        } else {
            event.setState(EventState.PENDING);
        }
        updateEvent(eventDto, event);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getUserRequestsByEventId(Long userId, Long eventId) {
        log.info("EVENT SERVICE: GET requests user id = {}, event id = {}", userId, eventId);

        getEventByEventIdAndInitiatorIdOrThrow(eventId, userId);

        return requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResponseDto patchUserRequestStatusForEvent(Long userId, Long eventId,
                                                                              EventRequestStatusUpdateRequestDto requestStatusesDto) {
        log.info("EVENT SERVICE: PATCH requests by event id = {}, user id = {}, requestStatus = {}", eventId, userId,
                requestStatusesDto);

        Event event = getEventByEventIdAndInitiatorIdOrThrow(eventId, userId);

        if (!event.getRequestModeration()
                || event.getParticipantLimit() == 0
                || requestStatusesDto.getRequestIds().isEmpty()
        ) {
            return new EventRequestStatusUpdateResponseDto(Collections.emptyList(), Collections.emptyList());
        }

        List<Request> requests = requestRepository.findAllById(requestStatusesDto.getRequestIds());

        if (requests.size() != requestStatusesDto.getRequestIds().size()) {
            throw new NotFoundEwmException(String.format("Found %d requests but expected %d", requests.size(),
                    requestStatusesDto.getRequestIds().size()));
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        long participantCount = requestRepository.countAllByEventIdAndStatusEquals(eventId, RequestStatuses.CONFIRMED);

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatuses.PENDING)) {
                throw new ForbiddenActionEwmException(
                        String.format("Status = %s. Only pending requests can be changed", request.getStatus()));
            }
            if (RequestStatuses.CONFIRMED.equals(RequestStatuses.valueOf(requestStatusesDto.getStatus()))) {
                if (participantCount >= event.getParticipantLimit()) {
                    throw new ForbiddenActionEwmException("Limit of participants has been reached");
                }
                request.setStatus(RequestStatuses.CONFIRMED);
                confirmedList.add(request);
                participantCount++;
            } else {
                request.setStatus(RequestStatuses.REJECTED);
                rejectedList.add(request);
            }
        }

        requestRepository.saveAll(confirmedList);
        requestRepository.saveAll(rejectedList);

        return new EventRequestStatusUpdateResponseDto(
                confirmedList.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejectedList.stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<EventShortResponseDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                                       String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                       String sort, Integer from, Integer size,
                                                       HttpServletRequest request) {
        log.info("EVENT SERVICE: get Public events text: {}, categories: {}, paid = {}, rangeStart: {}, rangeEnd: {}," +
                        "onlyAvailable = {}, sort = {}, from = {}, size = {}, request = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        Sort sortOrder = Sort.by(Sort.Direction.ASC, "eventDate");
        Pageable pageable = PageRequest.of(from / size, size, sortOrder);

        LocalDateTime startDate =
                rangeStart != null ? LocalDateTime.parse(rangeStart, TIMESTAMP_FORMATTER) : LocalDateTime.now();
        LocalDateTime endDate =
                rangeEnd != null ? LocalDateTime.parse(rangeEnd, TIMESTAMP_FORMATTER) : LocalDateTime.now().plusYears(100);

        if (startDate.isAfter(endDate)) {
            throw new ValidationEwmException("The start date of the search cannot be later than the end date");
        }

        // создание Predicate для Querydsl
        BooleanBuilder builder = new BooleanBuilder();
        Optional.ofNullable(text).ifPresent(t -> builder.and(QEvent.event.annotation.likeIgnoreCase(t)
                .or(QEvent.event.description.likeIgnoreCase(t))));
        Optional.ofNullable(categories).ifPresent(c -> builder.and(QEvent.event.category.id.in(c)));
        Optional.ofNullable(paid).ifPresent(p -> builder.and(QEvent.event.paid.eq(p)));
        builder.and(QEvent.event.state.eq(EventState.PUBLISHED));
        builder.and(QEvent.event.eventDate.between(startDate, endDate));

        List<Event> events = eventRepository.findAll(builder.getValue(), pageable).getContent();

        eventStatsClient.addHit(request);

        Map<Long, Long> confirmedRequestsPerEvent = getConfirmedRequestsPerEvent(events);
        Map<Long, Long> viewsPerEvent = eventStatsClient.getViewsPerEvent(events);

        List<EventShortResponseDto> result = events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsPerEvent.getOrDefault(event.getId(), 0L),
                        viewsPerEvent.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        if ("VIEWS".equals(sort)) {
            result.sort(Comparator.comparing(dto -> viewsPerEvent.getOrDefault(dto.getId(), 0L)));
        }
        return result;
    }

    @Override
    public EventFullResponseDto getPublicEventById(Long eventId, HttpServletRequest request) {
        log.info("EVENT SERVICE: get Public event id = {}, request = {}", eventId, request);

        Event event = eventRepository.findByIdAndStateEquals(eventId, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundEwmException(String.format("Published event with id = %d not found.", eventId)));

        eventStatsClient.addHit(request);

        long confirmedRequests = requestRepository.countAllByEventIdAndStatusEquals(eventId, RequestStatuses.CONFIRMED);
        long views = eventStatsClient.getViews(eventId);
        List<CommentShortResponseDto> comments = commentRepository.findAllByEventIdAndStateEquals(eventId, CommentState.PUBLISHED)
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());

        return EventMapper.toEventFullDto(event, confirmedRequests, views, comments);
    }

    @Override
    public List<EventFullResponseDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                                     String rangeStart, String rangeEnd, Integer from, Integer size) {
        log.info(
                "EVENT SERVICE: GET full admin events. Users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        // создание Predicate для Querydsl
        BooleanBuilder builder = new BooleanBuilder();
        Optional.ofNullable(users).ifPresent(u -> builder.and(QEvent.event.initiator.id.in(u)));
        Optional.ofNullable(states)
                .ifPresent(s -> builder.and(
                        QEvent.event.state.in(s.stream().map(EventState::valueOf).collect(Collectors.toList()))));
        Optional.ofNullable(categories).ifPresent(c -> builder.and(QEvent.event.category.id.in(c)));
        Optional.ofNullable(rangeStart)
                .ifPresent(rs -> builder.and(QEvent.event.eventDate.after(LocalDateTime.parse(rs, TIMESTAMP_FORMATTER))));
        Optional.ofNullable(rangeEnd)
                .ifPresent(re -> builder.and(QEvent.event.eventDate.before(LocalDateTime.parse(re, TIMESTAMP_FORMATTER))));

        Predicate predicate = builder.getValue();
        List<Event> events;
        if (predicate != null) {
            events = eventRepository.findAll(predicate, pageable).getContent();
        } else {
            events = eventRepository.findAll(pageable).getContent();
        }

        Map<Long, Long> confirmedRequestsPerEvent = getConfirmedRequestsPerEvent(events);
        Map<Long, Long> viewsPerEvent = eventStatsClient.getViewsPerEvent(events);
        Map<Long, List<CommentShortResponseDto>> comments = getCommentsPerEvent(events);

        return events.stream()
                .map(event -> EventMapper.toEventFullDto(
                        event,
                        confirmedRequestsPerEvent.getOrDefault(event.getId(), 0L),
                        viewsPerEvent.getOrDefault(event.getId(), 0L),
                        comments.getOrDefault(event.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullResponseDto patchAdminEvent(EventUpdateRequestDto eventDto, Long eventId) {
        log.info("EVENT SERVICE: PATCH admin event: {}, id = {}", eventDto, eventId);

        checkTimeEvent(eventDto.getEventDate());
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundEwmException(String.format("Event id = %d not found", eventId)));

        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals("PUBLISH_EVENT")) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ForbiddenActionEwmException("Event is already published");
                }
                if (event.getState().equals(EventState.CANCELED)) {
                    throw new ForbiddenActionEwmException("Event is canceled");
                }
                event.setState(EventState.PUBLISHED);
            }
            if (eventDto.getStateAction().equals("REJECT_EVENT")) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ForbiddenActionEwmException(
                            "an event can only be rejected if it has not yet been published");
                }
                if (event.getState().equals(EventState.CANCELED)) {
                    throw new ForbiddenActionEwmException("Event is already canceled");
                }
                event.setState(EventState.CANCELED);
            }
        }
        updateEvent(eventDto, event);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundEwmException(String.format("User with id = %d not found.", userId)));
    }

    private Event getEventByEventIdAndInitiatorIdOrThrow(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundEwmException(
                        String.format("Event id = %d, user id = %d not found", eventId, userId)));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundEwmException(String.format("Category with id = %d not found.", categoryId)));
    }

    private Location getOrSaveLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElse(locationRepository.save(location));
    }

    private void checkTimeEvent(String timeEvent) {
        if (timeEvent != null &&
                LocalDateTime.parse(timeEvent, TIMESTAMP_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationEwmException(
                    "the date and time on which the event is scheduled cannot be earlier than two hours from the current moment."
            );
        }
    }

    private Map<Long, Long> getConfirmedRequestsPerEvent(List<Event> events) {
        List<Long> eventsIds = getEventsIds(events);
        List<Request> confirmedRequests =
                requestRepository.findAllByEventIdInAndStatusEquals(eventsIds, RequestStatuses.CONFIRMED);
        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(req -> req.getEvent().getId(), Collectors.counting()));
    }

    private Map<Long, List<CommentShortResponseDto>> getCommentsPerEvent(List<Event> events) {
        List<Long> eventsIds = getEventsIds(events);
        List<Comment> comments = commentRepository.findAllByEventIdInAndStateEquals(eventsIds, CommentState.PUBLISHED);
        return comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.groupingBy(CommentShortResponseDto::getEvent));
    }

    private List<Long> getEventsIds(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private void updateEvent(EventUpdateRequestDto eventDto, Event event) {
        Optional.ofNullable(eventDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventDto.getCategory()).map(this::getCategoryOrThrow).ifPresent(event::setCategory);
        Optional.ofNullable(eventDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventDto.getEventDate()).map(date -> LocalDateTime.parse(date, TIMESTAMP_FORMATTER))
                .ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getLocation()).map(LocationMapper::toLocation).map(this::getOrSaveLocation)
                .ifPresent(event::setLocation);
        Optional.ofNullable(eventDto.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(eventDto.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(eventDto.getTitle()).ifPresent(event::setTitle);
    }
}