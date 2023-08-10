package ru.practicum.explore_with_me.compilation.service;

import ru.practicum.explore_with_me.compilation.dto.CompilationCreateRequestDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationResponseDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationUpdateRequestDto;

import java.util.List;

public interface CompilationService {
    List<CompilationResponseDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationResponseDto getCompilationById(Long compId);

    CompilationResponseDto createCompilation(CompilationCreateRequestDto compilationDto);

    void deleteCompilation(Long compId);

    CompilationResponseDto updateCompilation(CompilationUpdateRequestDto compilationDto, Long compId);
}
