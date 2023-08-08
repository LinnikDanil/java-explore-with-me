package ru.practicum.explore_with_me.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.compilation.dto.CompilationCreateRequestDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationResponseDto;
import ru.practicum.explore_with_me.compilation.dto.CompilationUpdateRequestDto;
import ru.practicum.explore_with_me.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto postCompilation(@Valid @RequestBody CompilationCreateRequestDto compilationDto) {
        log.info("ADMIN CONTROLLER: POST compilation: {}", compilationDto);
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("ADMIN CONTROLLER: DELETE compilation id: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto patchCompilation(@RequestBody CompilationUpdateRequestDto compilationDto,
                                                   @PathVariable Long compId) {
        log.info("ADMIN CONTROLLER: PATCH compilation: {}, id = {}", compilationDto, compId);
        return compilationService.updateCompilation(compilationDto, compId);
    }
}
