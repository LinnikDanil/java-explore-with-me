package ru.practicum.explore_with_me.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank
    @Size(min = 30, max = 1000)
    private String text;
}
