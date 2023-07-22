package ru.practicum.explore_with_me.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public static Category toCategory(CategoryRequestDto categoryRequestDto) {
        return Category.builder()
                .name(categoryRequestDto.getName())
                .build();
    }

    public static Category toCategory(CategoryRequestDto categoryRequestDto, Long catId) {
        return Category.builder()
                .id(catId)
                .name(categoryRequestDto.getName())
                .build();
    }

    public static CategoryResponseDto toCategoryDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
