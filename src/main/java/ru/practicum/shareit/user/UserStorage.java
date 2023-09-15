package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User delete(int userId);

    List<User> getUsers();

    User getUserById(int userId);

    boolean existsById(int userId);
}
