package ru.practicum.explore_with_me.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explore_with_me.error.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.NotFoundEwmException;
import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@mail.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@mail.ru")
            .build();
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("Test of getting users by id list")
    void getUsersByIds() {
        List<User> users = Arrays.asList(user1, user2);
        List<Long> ids = users.stream().map(User::getId).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAllByIdIn(ids, pageable)).thenReturn(new PageImpl<>(users));

        List<UserResponseDto> userResponseDtos = userService.getUsers(ids, 0, 10);

        assertEquals(2, userResponseDtos.size());
        verify(userRepository, times(1)).findAllByIdIn(ids, pageable);
    }

    @Test
    @DisplayName("Test creating user with existing name")
    void createUserWithExistingName() {
        // Создание нового DTO с именем, которое уже существует в репозитории
        UserRequestDto userRequestDto = new UserRequestDto("userTest@mail.ru", "userTest");

        // Настройка репозитория для возвращения существующего пользователя при вызове findByName()
        when(userRepository.findByName(userRequestDto.getName())).thenReturn(Optional.of(user1));

        // Проверка, что AlreadyExistEwmException выбрасывается при попытке создать пользователя с уже существующим именем
        Exception exception = assertThrows(
                AlreadyExistEwmException.class,
                () -> userService.createUser(userRequestDto)
        );

        // Проверка, что сообщение исключения содержит ожидаемое сообщение
        String expectedMessage = String.format("A user named %s already exists", userRequestDto.getName());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Проверка, что метод findByName() был вызван один раз
        verify(userRepository, times(1)).findByName(userRequestDto.getName());
    }


    @Test
    @DisplayName("Test deleting a non-existent user")
    void deleteUserNotFound() {
        Long userId = 3L;  // This ID doesn't correspond to any user
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                NotFoundEwmException.class, () -> userService.deleteUser(userId));

        String expectedMessage = String.format("user with id = %d not found", userId);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(userId);
    }
}