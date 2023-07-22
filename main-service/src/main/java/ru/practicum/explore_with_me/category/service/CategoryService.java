package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(CategoryRequestDto categoryRequestDto, Long catId);

    void deleteCategory(Long catId);

    List<CategoryResponseDto> getCategories(Integer from, Integer size);

    CategoryResponseDto getCategoryById(Long catId);
}
