package ru.practicum.explore_with_me.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.error.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.NotFoundEwmException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        log.info("SERVICE: CREATE category: {}", categoryRequestDto);

        String updatedName = categoryRequestDto.getName();
        categoryRepository.findByName(updatedName)
                .ifPresent(e -> {
                    throw new AlreadyExistEwmException(String.format("A category named %s already exists", updatedName));
                });

        CategoryResponseDto categoryResponseDto = CategoryMapper.toCategoryDto(
                categoryRepository.save(CategoryMapper.toCategory(categoryRequestDto))
        );

        log.info("categoryResponseDto: {}", categoryResponseDto);
        return categoryResponseDto;
    }

    @Transactional
    @Override
    public CategoryResponseDto updateCategory(CategoryRequestDto categoryRequestDto, Long catId) {
        log.info("SERVICE: UPDATE category: {}, id = {}", categoryRequestDto, catId);

        Category oldCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundEwmException(String.format("Category with id = %d not found", catId)));

        String updatedName = categoryRequestDto.getName();
        if (!oldCategory.getName().equals(updatedName)) {
            categoryRepository.findByName(updatedName)
                    .ifPresent(e -> {
                        throw new AlreadyExistEwmException(String.format("A category named %s already exists", updatedName));
                    });
        }

        Category updatedCategory = CategoryMapper.toCategory(categoryRequestDto, catId);
        CategoryResponseDto categoryResponseDto = CategoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));

        log.info("categoryResponseDto: {}", categoryResponseDto);
        return categoryResponseDto;
    }


    @Override
    public void deleteCategory(Long catId) {
        log.info("SERVICE: DELETE category id = {}", catId);

        categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundEwmException(String.format("Category with id = %d not found", catId)));

        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryResponseDto> getCategories(Integer from, Integer size) {
        log.info("SERVICE: GET categories: from = {}, size = {}", from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        List<CategoryResponseDto> categoryResponseDtoList = categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        log.info("categoryResponseDtoList: {}", categoryResponseDtoList);
        return categoryResponseDtoList;
    }

    @Override
    public CategoryResponseDto getCategoryById(Long catId) {
        log.info("SERVICE: GET category id = {}", catId);

        CategoryResponseDto categoryResponseDto = CategoryMapper.toCategoryDto(
                categoryRepository.findById(catId).orElseThrow(() ->
                        new NotFoundEwmException(String.format("Category with id = %d not found", catId))));

        log.info("categoryResponseDto: {}", categoryResponseDto);
        return categoryResponseDto;
    }
}