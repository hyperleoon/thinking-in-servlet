package com.hyperleon.research.web.app.service;

import com.hyperleon.research.web.app.domain.User;

import java.util.Collection;

/**
 * @author leon
 * @date 2021-03-08 22:22
 **/
public interface UserService {

    boolean register(User user);


    boolean deregister(User user);

    boolean update(User user);

    User queryUserById(Long id);

    Collection<User> getAll();

    User queryUserByNameAndPassword(String name, String password);
}
