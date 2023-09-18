package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("UserStorage")
@Slf4j
public class UserStorageImpl implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(generateNewId());
        users.put(user.getId(), user);

        log.info("New user was created: id='{}', name = '{}'",
                user.getId(), user.getName());

        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }

        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId() == (user.getId()))) {

            users.put(user.getId(), user);

            log.info("User info was updated: id='{}', name = '{}'",
                    user.getId(), user.getName());
        } else {
            log.error("User with email='{}' already exists ", user.getEmail());
            throw new UserAlreadyExistsException("User with provided email " + user.getEmail() + " already exists");
        }
        return user;
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User delete(int userId) {
        return users.remove(userId);
    }

    @Override
    public boolean existsById(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));

    }

    private int generateNewId() {
        return ++id;
    }
}
