package ru.practicum.explore_with_me.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByIdIn(List<Long> userIds, Pageable pageable);
}
