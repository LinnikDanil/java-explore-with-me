package ru.practicum.explore_with_me.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.CommentState;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventInitiatorId(long initiatorId);

    Optional<Comment> findByIdAndStateEquals(long commentId, CommentState state);

    Optional<Comment> findFirstByAuthorIdAndEventIdAndStateIn(long authorId, long eventId, List<CommentState> states);

    List<Comment> findAllByStateEquals(CommentState state, Pageable pageable);

    List<Comment> findAllByEventIdInAndStateEquals(List<Long> eventsIds, CommentState state);

    List<Comment> findAllByEventIdAndStateEquals(long eventId, CommentState state);
}
