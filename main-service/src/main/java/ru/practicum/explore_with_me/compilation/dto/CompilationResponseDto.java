package ru.practicum.explore_with_me.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.event.dto.EventShortResponseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponseDto {
    List<EventShortResponseDto> events;
    Long id;
    Boolean pinned;
    String title;
}
