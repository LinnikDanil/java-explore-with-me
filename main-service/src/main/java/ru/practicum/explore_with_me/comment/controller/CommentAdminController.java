package ru.practicum.explore_with_me.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentFullResponseDto;
import ru.practicum.explore_with_me.comment.dto.ReportResponseDto;
import ru.practicum.explore_with_me.comment.service.CommentService;
import ru.practicum.explore_with_me.utils.CommentPageConstants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullResponseDto> getComments(@RequestParam(required = false) String stateAction,
                                                    @RequestParam(defaultValue = CommentPageConstants.PAGE_FROM)
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = CommentPageConstants.PAGE_SIZE)
                                                    @Positive Integer size) {
        log.info("ADMIN CONTROLLER: GET all comments stateAction = {}, from = {}, size = {}", stateAction, from, size);
        return commentService.getComments(stateAction, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullResponseDto getCommentById(@PathVariable Long commentId) {
        log.info("ADMIN CONTROLLER: GET comment by id = {}", commentId);
        return commentService.getCommentById(commentId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullResponseDto patchCommentStatus(@PathVariable Long commentId,
                                                     @RequestParam Boolean isPublish) {
        log.info("ADMIN CONTROLLER: PATCH comment status = {} comment id = {}", isPublish, commentId);
        return commentService.updateCommentStatus(isPublish, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("ADMIN CONTROLLER: DELETE comment id = {}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/reports")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportResponseDto> getAllReports(@RequestParam(defaultValue = CommentPageConstants.PAGE_FROM)
                                                 @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = CommentPageConstants.PAGE_SIZE)
                                                 @Positive Integer size) {
        log.info("ADMIN CONTROLLER: get all reports: from = {}, size = {}", from, size);
        return commentService.getCommentReports(from, size);
    }
}