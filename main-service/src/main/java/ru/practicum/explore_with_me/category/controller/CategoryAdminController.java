package ru.practicum.explore_with_me.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("ADMIN CONTROLLER: POST category: {}", categoryRequestDto);
        return categoryService.createCategory(categoryRequestDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto updateCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto,
                                              @PathVariable Long catId) {
        log.info("ADMIN CONTROLLER: PATCH category: {}, id = {}", categoryRequestDto, catId);
        return categoryService.updateCategory(categoryRequestDto, catId);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("ADMIN CONTROLLER: DELETE category id = {}", catId);
        categoryService.deleteCategory(catId);
    }
}