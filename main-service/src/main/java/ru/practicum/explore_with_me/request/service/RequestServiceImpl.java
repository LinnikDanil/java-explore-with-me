package ru.practicum.explore_with_me.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.error.exception.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.exception.ForbiddenActionEwmException;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.model.enums.EventState;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.model.Request;
import ru.practicum.explore_with_me.request.model.RequestStatuses;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        log.info("REQUEST SERVICE: GET requests, userId = {}", userId);
        getUserOrThrow(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
            .map(RequestMapper::toParticipationRequestDto)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("REQUEST SERVICE: CREATE requests, userId = {}", userId);

        User requester = getUserOrThrow(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(
            () -> new NotFoundEwmException(String.format("Event with id = %d not found.", eventId)));

        requestRepository.findByRequesterIdAndEventId(userId, eventId)
            .ifPresent(e -> {
                throw new AlreadyExistEwmException(
                    String.format("Request for event id = %d and user id = %d already exists", eventId, userId));
            });

        validateEvent(userId, eventId, event);

        long currentParticipants =
            requestRepository.countAllByEventIdAndStatusEquals(eventId, RequestStatuses.CONFIRMED);
        if (event.getParticipantLimit() != 0 && currentParticipants >= event.getParticipantLimit()) {
            throw new ForbiddenActionEwmException(
                String.format("Participant limit reached for event id = %d", eventId));
        }
        RequestStatuses status = (event.getRequestModeration() && event.getParticipantLimit() != 0)
            ? RequestStatuses.PENDING : RequestStatuses.CONFIRMED;

        return RequestMapper.toParticipationRequestDto(requestRepository.save(
            RequestMapper.toRequest(LocalDateTime.now(), event, requester, status)));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("REQUEST SERVICE: cancel requests, userId = {}", userId);

        getUserOrThrow(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
            () -> new NotFoundEwmException(String.format("Request with id = %d not found.", requestId)));
        request.setStatus(RequestStatuses.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NotFoundEwmException(String.format("User with id = %d not found.", userId)));
    }

    private void validateEvent(Long userId, Long eventId, Event event) {
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenActionEwmException(
                String.format("Owner id = %d cannot submit a request to participate in their event id = %d", userId,
                    eventId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenActionEwmException(
                String.format("User id = %d can't participate in an unpublished event id = %d", userId, eventId));
        }
    }
}