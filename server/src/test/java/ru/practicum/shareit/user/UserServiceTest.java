package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("UserName")
                .email("UserEmail@gmail.com")
                .build();
    }

    @Test
    void createUserTest() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(false);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void createUserWithEmailValidationExceptionTest() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        EmailValidationException exception = assertThrows(EmailValidationException.class,
                () -> userService.createUser(userDto));

        assertNotNull(exception);
    }

    @Test
    void updateUserTest() {
        User updatedUser = User.builder()
                .id(1L)
                .name("UpdatedName")
                .email("UserEmail@gmail.com")
                .build();

        when(userRepository.existsByEmail(any()))
                .thenReturn(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));
        when(userRepository.save(any()))
                .thenReturn(updatedUser);

        userDto.setName("UpdatedName");
        UserDto result = userService.updateUser(user.getId(), userDto);

        assertNotNull(result);
        assertEquals("UpdatedName", result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateUserWithEmailValidationExceptionTest() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(true);

        EmailValidationException exception = assertThrows(EmailValidationException.class,
                () -> userService.updateUser(user.getId(), userDto));

        assertNotNull(exception);
    }

    @Test
    void getUserByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(ofNullable(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserByIdWithNotFoundExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(10L));

        assertNotNull(exception);
    }

    @Test
    void getUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getId(), result.get(0).getId());
        assertEquals(userDto.getName(), result.get(0).getName());
    }

    @Test
    void deleteUserTest() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
