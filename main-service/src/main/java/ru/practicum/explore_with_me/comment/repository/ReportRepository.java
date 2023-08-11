package ru.practicum.explore_with_me.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.comment.model.Report;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByCommentIdAndOwnerId(long commentId, long ownerId);
}
