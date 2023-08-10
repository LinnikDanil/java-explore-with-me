package ru.practicum.explore_with_me.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import ru.practicum.explore_with_me.utils.CompilationSizeConstants;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationUpdateRequestDto {
    @UniqueElements
    List<Long> events;

    Boolean pinned;

    @Size(min = CompilationSizeConstants.TITLE_SIZE_MIN, max = CompilationSizeConstants.TITLE_SIZE_MAX, message = "Title length should be between 1 and 50 characters")
    String title;
}
