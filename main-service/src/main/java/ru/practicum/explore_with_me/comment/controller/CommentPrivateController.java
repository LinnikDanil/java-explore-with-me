package ru.practicum.explore_with_me.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentFullResponseDto;
import ru.practicum.explore_with_me.comment.dto.CommentRequestDto;
import ru.practicum.explore_with_me.comment.dto.ReportRequestDto;
import ru.practicum.explore_with_me.comment.dto.ReportResponseDto;
import ru.practicum.explore_with_me.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullResponseDto postComment(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("PRIVATE CONTROLLER: POST comment: {}, userId = {}, eventId = {}", commentRequestDto, userId, eventId);
        return commentService.createComment(commentRequestDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullResponseDto patchComment(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @PathVariable Long commentId,
                                               @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("PRIVATE CONTROLLER: PATCH comment: {} id = {}, userId = {}, eventId = {}",
                commentRequestDto, commentId, userId, eventId);
        return commentService.updateComment(commentRequestDto, userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.info("PRIVATE CONTROLLER: DELETE comment id = {}, userId = {}, eventId = {}", commentId, userId, eventId);
        commentService.deleteCommentByUser(userId, eventId, commentId);
    }

    @PostMapping("/{commentId}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDto postCommentReport(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @PathVariable Long commentId,
                                               @Valid @RequestBody ReportRequestDto reportRequestDto) {
        log.info("PRIVATE CONTROLLER: POST report: {}, commentId = {}, userId = {}, eventId = {}",
                reportRequestDto, commentId, userId, eventId);
        return commentService.createCommentReport(reportRequestDto, userId, eventId, commentId);
    }
}