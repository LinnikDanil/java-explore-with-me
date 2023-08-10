package ru.practicum.explore_with_me.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.category.dto.CategoryResponseDto;
import ru.practicum.explore_with_me.location.dto.LocationDto;
import ru.practicum.explore_with_me.user.dto.UserShortResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullResponseDto {
    String annotation; //Краткое описание
    CategoryResponseDto category;
    Long confirmedRequests; //Количество одобренных заявок на учаестие в данном событии
    String createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    String description; //Полное описание
    String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    Long id;
    UserShortResponseDto initiator;
    LocationDto location;
    Boolean paid; //Платное ли участие
    Integer participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    Boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    String state; //Список состояний жизненного цикла события
    String title; //Заголовок
    Long views; //Просмотры
}