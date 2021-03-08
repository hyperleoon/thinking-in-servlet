package com.hyperleon.research.web.app.service.impl;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.app.repository.RepositoryProxy;
import com.hyperleon.research.web.app.repository.UserRepository;
import com.hyperleon.research.web.app.service.UserService;

import java.sql.SQLException;
import java.util.Collection;

/**
 * @author leon
 * @date 2021-03-08 22:23
 **/
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    {
        try {
            userRepository = RepositoryProxy.create(UserRepository.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return  userRepository.getAll();
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
