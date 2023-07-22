package ru.practicum.explore_with_me.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
