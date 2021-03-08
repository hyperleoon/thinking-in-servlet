package com.hyperleon.research.web.app.repository;

import com.hyperleon.research.web.framework.orm.DBConnectionManager;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 * @date 2021-03-08 22:24
 **/
public class RepositoryProxy {
    private static Map<String, Object> repositoryMap = new ConcurrentHashMap<>();

    public static <T> T create(Class clazz) throws SQLException {
        if (!repositoryMap.containsKey(clazz.getName())) {
            repositoryMap.put(clazz.getName(), newProxy(clazz));
        }
        return (T) repositoryMap.get(clazz.getName());
    }

    private static <T> T newProxy(Class clazz) throws SQLException {
        ClassLoader loader = RepositoryProxy.class.getClassLoader();
        Class[] classes = new Class[]{clazz};
        DBConnectionManager dbConnectionManager= new DBConnectionManager();
        return (T) Proxy.newProxyInstance(loader, classes, new DatabaseUserRepository(dbConnectionManager));
    }
}