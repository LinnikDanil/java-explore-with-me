package ru.practicum.explore_with_me.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateStateRequestDto {
    @NotBlank
    private String state;
}
