package ru.practicum.explore_with_me.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @Email(message = "email is not valid")
    @NotNull(message = "email can't be null")
    private String email;
    @NotBlank(message = "name can't be blank")
    private String name;
}
