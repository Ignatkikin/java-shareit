package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        checkUserEmail(user);
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Пользователь {}  c id {} успешно добавлен", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        checkIfUserExists(newUser.getId());
        checkUserEmail(newUser);
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        log.info("Пользователь {} c id {} успешно обновлен", oldUser.getName(), oldUser.getId());
        return oldUser;
    }


    @Override
    public User getUserById(Long id) {
        checkIfUserExists(id);
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        checkIfUserExists(id);
        users.remove(id);
        log.info("Пользователь с id {} успешно удален", id);
    }

    @Override
    public void checkIfUserExists(Long id) {
        if (!users.containsKey(id)) {
            log.error("пользователь с id {} не найден", id);
            throw new NotFoundException("пользователь с id " + id + " не найден");
        }
    }

    private void checkUserEmail(User user) {
        for (User users : getUsers()) {
            if (users.getEmail().equals(user.getEmail())) {
                log.error("Email {} занят другим пользователем", user.getEmail());
                throw new EmailValidationException("Email " + user.getEmail() + " занят");
            }
        }
    }

    private long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
