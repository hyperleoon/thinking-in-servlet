package com.hyperleon.research.web.service.impl;

import com.hyperleon.research.web.domain.User;
import com.hyperleon.research.web.repository.RepositoryProxy;
import com.hyperleon.research.web.repository.UserRepository;
import com.hyperleon.research.web.service.UserService;

import java.sql.SQLException;

/**
 * @author leon
 * @date 2021-03-03 21:01
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
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
