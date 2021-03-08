package com.hyperleon.research.web.app.repository;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.framework.orm.Insert;
import com.hyperleon.research.web.framework.orm.Select;

import java.util.Collection;

/**
 * @author leon
 * @date 2021-03-08 22:24
 **/
public interface UserRepository {
    @Insert("insert into users (%s) values (%s)")
    boolean save(User user);

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    @Select(value = "select * from users", returnType = "org.geektimes.projects.user.domain.User")
    Collection<User> getAll();
}
