package ru.practicum.explore_with_me.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.user.dto.UserShortResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortResponseDto {
    String annotation; //Краткое описание
    CategoryResponseDto category;
    Long confirmedRequests; //Количество одобренных заявок на участие в данном событии
    String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    Long id;
    UserShortResponseDto initiator;
    Boolean paid; //Платное ли участие
    String title; //Заголовок
    Long views; //Просмотры
}
