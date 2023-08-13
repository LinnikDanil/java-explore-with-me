package ru.practicum.explore_with_me.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.comment.dto.ReportRequestDto;
import ru.practicum.explore_with_me.comment.dto.ReportResponseDto;
import ru.practicum.explore_with_me.comment.model.Comment;
import ru.practicum.explore_with_me.comment.model.Report;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.utils.EventTimeFormatConstants.TIMESTAMP_FORMATTER;

@UtilityClass
public class ReportMapper {
    public static ReportResponseDto toReportDto(Report report) {
        return ReportResponseDto.builder()
                .id(report.getId())
                .text(report.getText())
                .owner(UserMapper.toUserShortDto(report.getOwner()))
                .event(EventMapper.toEventShortDto(report.getEvent()))
                .comment(CommentMapper.toCommentShortDto(report.getComment()))
                .createdDate(report.getCreatedDate().format(TIMESTAMP_FORMATTER))
                .build();
    }

    public static Report toReport(ReportRequestDto reportDto, Comment comment, User user, Event event) {
        return Report.builder()
                .text(reportDto.getText())
                .owner(user)
                .event(event)
                .comment(comment)
                .createdDate(LocalDateTime.now())
                .build();
    }
}
