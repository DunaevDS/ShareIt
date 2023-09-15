package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("UserStorage")
@Slf4j
public class UserStorageImpl implements UserStorage {

    public Map<Integer, User> users = new HashMap<>();
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
        users.put(user.getId(), user);

        log.info("User info was updated: id='{}', name = '{}'",
                user.getId(), user.getName());

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

    private int generateNewId() {
        return ++id;
    }
}
