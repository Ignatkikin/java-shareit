package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto newUserDto);

    UserDto getUserById(Long id);

    void delete(Long id);
}
