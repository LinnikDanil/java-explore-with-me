package ru.practicum.explore_with_me.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.comment.dto.CommentShortResponseDto;
import ru.practicum.explore_with_me.comment.service.CommentService;
import ru.practicum.explore_with_me.utils.CommentPageConstants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortResponseDto> getCommentsByUser(@PathVariable Long userId,
                                                           @RequestParam(defaultValue = CommentPageConstants.PAGE_FROM)
                                                           @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = CommentPageConstants.PAGE_SIZE)
                                                           @Positive Integer size) {
        log.info("PUBLIC CONTROLLER: GET comments of the user with id = {}, from = {}, size = {}", userId, from, size);
        return commentService.getCommentsByUserId(userId, from, size);
    }
}
