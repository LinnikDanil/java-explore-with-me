package ru.practicum.explore_with_me.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotNull(message = "email can't be null")
    @Email(message = "email is not valid")
    @Size(min = 6, max = 254, message = "Email length should be between 6 and 254 characters")
    private String email;

    @Size(min = 2, max = 250, message = "Name length should be between 2 and 250 characters")
    @NotBlank(message = "name can't be blank")
    private String name;
}