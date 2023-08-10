package ru.practicum.explore_with_me.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;
import ru.practicum.explore_with_me.user.dto.UserShortResponseDto;
import ru.practicum.explore_with_me.user.model.User;

@UtilityClass
public class UserMapper {
    public static UserResponseDto toUserDto(User user) {
        return UserResponseDto.builder()
            .email(user.getEmail())
            .id(user.getId())
            .name(user.getName())
            .build();
    }

    public static User toUser(UserRequestDto userRequestDto) {
        return User.builder()
            .email(userRequestDto.getEmail())
            .name(userRequestDto.getName())
            .build();
    }

    public static UserShortResponseDto toUserShortDto(User user) {
        return UserShortResponseDto.builder()
            .id(user.getId())
            .name(user.getName())
            .build();
    }
}
