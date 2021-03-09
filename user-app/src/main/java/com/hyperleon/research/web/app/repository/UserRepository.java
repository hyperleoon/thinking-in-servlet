package com.hyperleon.research.web.app.repository;

import com.hyperleon.research.web.app.domain.User;

import java.util.Collection;

/**
 * @author leon
 * @date 2021-03-08 22:24
 **/
public interface UserRepository {
    boolean save(User user);

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();
}
