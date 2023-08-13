package ru.practicum.explore_with_me.comment.service;

import ru.practicum.explore_with_me.comment.dto.*;

import java.util.List;

public interface CommentService {

    List<CommentShortResponseDto> getCommentsByUserId(Long userId, Integer from, Integer size);

    CommentFullResponseDto createComment(CommentRequestDto commentRequestDto, Long userId, Long eventId);

    CommentFullResponseDto updateComment(CommentRequestDto commentRequestDto, Long userId, Long eventId, Long commentId);

    void deleteCommentByUser(Long userId, Long eventId, Long commentId);

    ReportResponseDto createCommentReport(ReportRequestDto reportRequestDto, Long userId, Long eventId, Long commentId);

    List<CommentFullResponseDto> getComments(String stateAction, Integer from, Integer size);

    CommentFullResponseDto updateCommentStatus(Boolean isPublish, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<ReportResponseDto> getCommentReports(Integer from, Integer size);

    CommentFullResponseDto getCommentById(Long commentId);
}
