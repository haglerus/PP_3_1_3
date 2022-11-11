package ru.kata.spring.boot_security.demo.dao;



import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {
    void add(User user);

    User getUser(Long id);

    User getUserByUsername(String username);

    List<User> listUsers();

    void update(User user);

    void delete(Long id);
}
