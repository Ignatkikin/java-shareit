package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User createUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);

    void delete(Long id);

    void checkIfUserExists(Long id);
}
