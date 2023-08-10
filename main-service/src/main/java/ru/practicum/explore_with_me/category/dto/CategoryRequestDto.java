package ru.practicum.explore_with_me.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    @NotBlank
    @Size(min = 1, max = 50, message = "Name length should be between 1 and 50 characters")
    private String name;
}
