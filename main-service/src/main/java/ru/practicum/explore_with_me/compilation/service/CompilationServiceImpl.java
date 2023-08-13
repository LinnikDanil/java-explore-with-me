package ru.practicum.explore_with_me.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilation.dto.CompilationCreateRequestDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationResponseDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationUpdateRequestDto;
import ru.practicum.explore_with_me.compilation.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.compilation.repository.CompilationRepository;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.request.model.RequestStatuses;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.utils.EventStatsClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventStatsClient eventStatsClient;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationResponseDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("COMPILATION SERVICE: GET compilations pinned = {}, from = {}, size = {}", pinned, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations = pinned != null ? compilationRepository.findAllByPinnedEquals(pinned, pageable)
                : compilationRepository.findAll(pageable).getContent();

        Set<Event> events = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        List<EventShortResponseDto> eventsDto = getEventShortDto(events);

        return CompilationMapper.toCompilationsDto(compilations, eventsDto);
    }

    @Override
    public CompilationResponseDto getCompilationById(Long compId) {
        log.info("COMPILATION SERVICE: GET compilation by id = {}", compId);

        Compilation compilation = getCompilationOrThrow(compId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationResponseDto createCompilation(CompilationCreateRequestDto compilationDto) {
        log.info("COMPILATION SERVICE: CREATE compilation: {}", compilationDto);
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllById(compilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(compilationDto, events)));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("COMPILATION SERVICE: DELETE compilation id: {}", compId);
        compilationRepository.delete(getCompilationOrThrow(compId));
    }

    @Override
    @Transactional
    public CompilationResponseDto updateCompilation(CompilationUpdateRequestDto compilationDto, Long compId) {
        log.info("COMPILATION SERVICE: UPDATE compilation: {}, id = {}", compilationDto, compId);

        Compilation compilation = getCompilationOrThrow(compId);

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(compilationDto.getEvents())));
        }

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    private List<EventShortResponseDto> getEventShortDto(Set<Event> events) {
        Map<Long, Long> viewsPerEvent = eventStatsClient.getViewsPerEvent(new ArrayList<>(events));

        List<Long> eventsIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> confirmedRequestsPerEvent =
                requestRepository.findAllByEventIdInAndStatusEquals(eventsIds, RequestStatuses.CONFIRMED)
                        .stream()
                        .collect(Collectors.groupingBy(req -> req.getEvent().getId(), Collectors.counting()));

        return events.stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsPerEvent.getOrDefault(event.getId(), 0L),
                        viewsPerEvent.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private Compilation getCompilationOrThrow(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundEwmException(String.format("Compilation with id = %d not found.", compId)));
    }
}
