package ru.practicum.explore_with_me.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.comment.dto.*;
import ru.practicum.explore_with_me.comment.mapper.CommentMapper;
import ru.practicum.explore_with_me.comment.mapper.ReportMapper;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.CommentState;
import ru.practicum.explore_with_me.comment.repository.CommentRepository;
import ru.practicum.explore_with_me.comment.repository.ReportRepository;
import ru.practicum.explore_with_me.error.exception.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.exception.ForbiddenActionEwmException;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;

    @Override
    public List<CommentShortResponseDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        log.info("COMMENT SERVICE: GET user comments, userId = {}, from = {}, size = {}", userId, from, size);

        getUserOrThrow(userId);

        return commentRepository.findAllByEventInitiatorId(userId).stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullResponseDto createComment(CommentRequestDto commentRequestDto, Long userId, Long eventId) {
        log.info("COMMENT SERVICE: CREATE comment text = {}, userId = {}, eventId = {}", commentRequestDto.getText(), userId, eventId);

        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        checkUserNotInitiatorEvent(event, userId);
        checkAlreadyExistComment(userId, eventId);

        return CommentMapper.toCommentFullDto(commentRepository.save(CommentMapper.toComment(commentRequestDto, user, event)));
    }

    @Override
    @Transactional
    public CommentFullResponseDto updateComment(CommentRequestDto commentRequestDto, Long userId, Long eventId,
                                                Long commentId) {
        log.info("COMMENT SERVICE: UPDATE comment text = {}, userId = {}, eventId = {}", commentRequestDto.getText(), userId, eventId);

        Comment comment = getCommentAndValidationData(userId, eventId, commentId);
        comment.setText(commentRequestDto.getText());

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long eventId, Long commentId) {
        log.info("COMMENT SERVICE: DELETE comment id = {}, userId = {}, eventId = {}", commentId, userId, eventId);

        Comment comment = getCommentAndValidationData(userId, eventId, commentId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public ReportResponseDto createCommentReport(ReportRequestDto reportRequestDto, Long userId, Long eventId,
                                                 Long commentId) {
        log.info("COMMENT SERVICE: CREATE report text = {}, to comment id = {}, userId = {}, eventId = {}",
                reportRequestDto.getText(), commentId, userId, eventId);

        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        Comment comment = getPublishedCommentOrThrow(commentId);
        checkUserIsInitiatorEvent(event, userId);
        checkAlreadyExistReport(commentId, userId);

        return ReportMapper.toReportDto(reportRepository.save(
                ReportMapper.toReport(reportRequestDto, comment, user, event)));
    }

    @Override
    public List<CommentFullResponseDto> getComments(String stateAction, Integer from, Integer size) {
        log.info("COMMENT SERVICE: GET all comments, stateAction = {}, from = {}, size = {}", stateAction, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        List<Comment> comments;
        if (stateAction == null) {
            comments = commentRepository.findAll(pageable).getContent();
        } else {
            comments = commentRepository.findAllByStateEquals(CommentState.valueOf(stateAction), pageable);
        }

        return comments.stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullResponseDto getCommentById(Long commentId) {
        log.info("COMMENT SERVICE: GET comment by id = {}", commentId);

        return CommentMapper.toCommentFullDto(getCommentOrThrow(commentId));
    }

    @Override
    @Transactional
    public CommentFullResponseDto updateCommentStatus(Boolean isPublish, Long commentId) {
        log.info("COMMENT SERVICE: UPDATE comment id = {} status isPublish = {}", commentId, isPublish);

        Comment comment = getCommentWithStatePendingOrThrow(commentId);

        if (isPublish) {
            comment.setState(CommentState.PUBLISHED);
            comment.setPublishedDate(LocalDateTime.now());
        } else {
            comment.setState(CommentState.REJECTED);
        }

        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        log.info("COMMENT SERVICE: DELETE comment id = {} by admin", commentId);

        commentRepository.delete(getCommentOrThrow(commentId));
    }

    @Override
    public List<ReportResponseDto> getCommentReports(Integer from, Integer size) {
        log.info("COMMENT SERVICE: GET comment reports from = {}, size = {}", from, size);

        return reportRepository.findAll().stream()
                .map(ReportMapper::toReportDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentAndValidationData(long userId, long eventId, long commentId) {
        getUserOrThrow(userId);
        getEventOrThrow(eventId);
        Comment comment = getCommentWithStatePendingOrThrow(commentId);
        checkUserIsAuthorComment(userId, comment);
        return comment;
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundEwmException(String.format("User with id = %d not found.", userId)));
    }

    private Event getEventOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundEwmException(String.format("Event with id = %d not found", eventId)));
    }

    private Comment getCommentOrThrow(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundEwmException(String.format("Comment with id = %d not found", commentId)));
    }

    private Comment getCommentWithStatePendingOrThrow(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundEwmException(String.format("Comment with id = %d not found", commentId)));
        if (!comment.getState().equals(CommentState.PENDING)) {
            throw new ForbiddenActionEwmException("Only events with PENDING status can be changed or deleted");
        }
        return comment;
    }

    private Comment getPublishedCommentOrThrow(long commentId) {
        return commentRepository.findByIdAndStateEquals(commentId, CommentState.PUBLISHED).orElseThrow(
                () -> new NotFoundEwmException(String.format("Published comment with id = %d not found", commentId)));
    }

    private void checkUserNotInitiatorEvent(Event event, long userId) {
        if (event.getInitiator().getId() == userId) {
            throw new ForbiddenActionEwmException("The owner of the event cannot leave comments for himself");
        }
    }

    private void checkUserIsInitiatorEvent(Event event, long userId) {
        if (event.getInitiator().getId() != userId) {
            throw new ForbiddenActionEwmException("Only the initiator of the event can create a report about the comments");
        }
    }

    private void checkUserIsAuthorComment(long userId, Comment comment) {
        if (userId != comment.getAuthor().getId()) {
            throw new ForbiddenActionEwmException("Only the author of the comment can edit it");
        }
    }

    private void checkAlreadyExistComment(long authorId, long eventId) {
        commentRepository.findFirstByAuthorIdAndEventIdAndStateIn(authorId, eventId, List.of(CommentState.PUBLISHED, CommentState.PENDING))
                .ifPresent((comment) -> {
                    throw new AlreadyExistEwmException(String.format("Comment with id = %d already exist", comment.getId()));
                });
    }

    private void checkAlreadyExistReport(long commentId, long ownerId) {
        reportRepository.findByCommentIdAndOwnerId(commentId, ownerId)
                .ifPresent((comment) -> {
                    throw new AlreadyExistEwmException(String.format("Report with id = %d by author = %d already exist", commentId, ownerId));
                });
    }
}