package ru.practicum.explore_with_me.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.compilation.dto.CompilationCreateRequestDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationResponseDto;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static List<CompilationResponseDto> toCompilationsDto(List<Compilation> compilations, List<EventShortResponseDto> eventsDto) {
        Map<Long, EventShortResponseDto> eventShortDtoMap = eventsDto.stream()
                .collect(Collectors.toMap(EventShortResponseDto::getId, Function.identity()));

        return compilations.stream()
                .map(compilation -> CompilationResponseDto.builder()
                        .title(compilation.getTitle())
                        .id(compilation.getId())
                        .pinned(compilation.getPinned())
                        .events(compilation.getEvents().stream()
                                .map(event -> eventShortDtoMap.get(event.getId()))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }


    public static CompilationResponseDto toCompilationDto(Compilation compilation) {
        return CompilationResponseDto.builder()
                .title(compilation.getTitle())
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Compilation toCompilation(CompilationCreateRequestDto compilationDto, Set<Event> events) {

        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .events(events)
                .build();
    }
}
