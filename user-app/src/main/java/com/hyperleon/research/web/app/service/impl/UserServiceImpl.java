package com.hyperleon.research.web.app.service.impl;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.app.service.UserService;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Validator;


/**
 * @author leon
 * @date 2021-03-08 22:23
 **/
public class UserServiceImpl implements UserService {

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public boolean register(User user) {
        entityManager.persist(user);
        return true;
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
