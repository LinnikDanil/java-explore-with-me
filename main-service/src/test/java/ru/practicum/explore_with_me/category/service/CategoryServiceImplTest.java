package ru.practicum.explore_with_me.category.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explore_with_me.category.dto.CategoryRequestDto;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.error.exception.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.event.repository.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private final Category category1 = Category.builder()
            .id(1L)
            .name("category1")
            .build();
    private final Category category2 = Category.builder()
            .id(2L)
            .name("category2")
            .build();

    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, eventRepository);
    }

    @Test
    @DisplayName("Test of updating category")
    void updateCategory() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("category2");
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));
        when(categoryRepository.findByName(categoryRequestDto.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category2);

        CategoryResponseDto updatedCategory = categoryService.updateCategory(categoryRequestDto, category1.getId());

        assertEquals(category2.getName(), updatedCategory.getName());
        verify(categoryRepository, times(1)).findById(category1.getId());
        verify(categoryRepository, times(1)).findByName(categoryRequestDto.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test updating non-existent category")
    void updateNonExistentCategory() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("category2");
        Long categoryId = 3L; // This ID doesn't correspond to any category
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundEwmException.class, () -> categoryService.updateCategory(categoryRequestDto, categoryId));

        String expectedMessage = String.format("Category with id = %d not found", categoryId);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Test updating category with existing name")
    void updateCategoryWithExistingName() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("category2");
        when(categoryRepository.findById(category1.getId())).thenReturn(Optional.of(category1));
        when(categoryRepository.findByName(categoryRequestDto.getName())).thenReturn(Optional.of(category2));

        Exception exception = assertThrows(
                AlreadyExistEwmException.class,
                () -> categoryService.updateCategory(categoryRequestDto, category1.getId()));

        String expectedMessage = String.format("A category named %s already exists", categoryRequestDto.getName());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(categoryRepository, times(1)).findById(category1.getId());
        verify(categoryRepository, times(1)).findByName(categoryRequestDto.getName());
    }

    @Test
    @DisplayName("Test creating category with existing name")
    void createCategoryWithExistingName() {
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("category1");

        when(categoryRepository.findByName(categoryRequestDto.getName())).thenReturn(Optional.of(category1));

        Exception exception = assertThrows(
                AlreadyExistEwmException.class,
                () -> categoryService.createCategory(categoryRequestDto)
        );

        String expectedMessage = String.format("A category named %s already exists", categoryRequestDto.getName());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(categoryRepository, times(1)).findByName(categoryRequestDto.getName());
    }


    @Test
    @DisplayName("Test deleting a non-existent category")
    void deleteCategoryNotFound() {
        Long categoryId = 3L;  // This ID doesn't correspond to any category
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundEwmException.class, () -> categoryService.deleteCategory(categoryId));

        String expectedMessage = String.format("Category with id = %d not found", categoryId);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Test getting a non-existent category by id")
    void getCategoryByIdNotFound() {
        Long categoryId = 3L;  // This ID doesn't correspond to any category
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundEwmException.class, () -> categoryService.getCategoryById(categoryId));

        String expectedMessage = String.format("Category with id = %d not found", categoryId);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(categoryRepository, times(1)).findById(categoryId);
    }
}