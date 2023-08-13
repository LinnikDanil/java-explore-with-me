package ru.practicum.explore_with_me.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;
import ru.practicum.explore_with_me.user.dto.UserShortResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponseDto {
    Long id;
    String text;
    UserShortResponseDto owner; //Владелец жалобы
    EventShortResponseDto event;
    CommentShortResponseDto comment;
    String createdDate;
}
