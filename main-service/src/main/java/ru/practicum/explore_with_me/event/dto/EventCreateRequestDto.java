package ru.practicum.explore_with_me.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static ru.practicum.explore_with_me.utils.EventSizeConstants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCreateRequestDto {
    @NotNull
    @Size(min = ANNOTATION_SIZE_MIN, max = ANNOTATION_SIZE_MAX, message = "Annotation should contain between 20 and 2000 characters")
    String annotation; //Краткое описание

    @NotNull
    Long category;

    @NotNull
    @Size(min = DESCRIPTION_SIZE_MIN, max = DESCRIPTION_SIZE_MAX, message = "Title should contain between 20 and 7000 characters")
    String description; //Полное описание

    @NotBlank
    String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @NotNull
    @Valid
    LocationDto location;

    Boolean paid = false; //Платное ли участие

    @PositiveOrZero
    Integer participantLimit = 0; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    Boolean requestModeration = true; //Нужна ли пре-модерация заявок на участие

    String stateAction; //Список состояний жизненного цикла события

    @NotNull
    @Size(min = TITLE_SIZE_MIN, max = TITLE_SIZE_MAX, message = "Title should contain between 3 and 120 characters")
    String title; //Заголовок
}
