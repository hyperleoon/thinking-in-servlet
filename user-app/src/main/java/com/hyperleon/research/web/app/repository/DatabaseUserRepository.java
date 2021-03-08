package com.hyperleon.research.web.app.repository;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.app.function.ThrowableFunction;
import com.hyperleon.research.web.framework.orm.DBConnectionManager;
import com.hyperleon.research.web.framework.orm.Insert;
import com.hyperleon.research.web.framework.orm.Select;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

/**
 * @author leon
 * @date 2021-03-08 22:31
 **/
public class DatabaseUserRepository implements UserRepository, InvocationHandler {
    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());

    public static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users(" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "name VARCHAR(16) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64) NOT NULL, " +
            "phoneNumber VARCHAR(64) NOT NULL" +
            ")";

    /**
     * 通用处理方式
     */
    private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());


    private final DBConnectionManager dbConnectionManager;

    public DatabaseUserRepository(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    private Connection getConnection() {
        String databaseURL ="jdbc:derby:TEST/db/user-platform;create=true";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseURL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }

    @Override
    public boolean save(User user) {
        return false;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?",
                resultSet -> new User(), COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users", resultSet -> {
            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                User user = new User();
                for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                    String fieldName = propertyDescriptor.getName();
                    Class fieldType = propertyDescriptor.getPropertyType();
                    String methodName = resultSetMethodMappings.get(fieldType);
                    String columnLabel = mapColumnLabel(fieldName);
                    Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                    Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
                    Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                    setterMethodFromUser.invoke(user, resultValue);
                    System.out.println(user.toString());
                }
            }

            return users;
        }, e -> {
            // 异常处理
        });
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, wrapperType);
                method.invoke(preparedStatement, i + 1, args);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }
    private Object executeInsertSql(String sqlTemplate, Object[] args) throws IllegalAccessException, SQLException {
        System.out.println("execute insert: " + sqlTemplate);
        StringBuilder cols = new StringBuilder();
        StringBuilder values = new StringBuilder();

        Object data = args[0];
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field: fields) {
            if (field.get(data) == null) {
                continue;
            }
            cols.append(field.getName()).append(",");
            String sp= field.get(data).toString();
            sp="'"+sp+"'";
            values.append(sp+",");
        }
        String sql = String.format(sqlTemplate, cols.toString().substring(0,cols.length()-1), values.toString().substring(0,values.length()-1));
        System.out.println("sql string: " + sql);
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resq = meta.getTables(null, null, null, new String[]{"TABLE"});
        HashSet<String> set=new HashSet<String>();
        while (resq.next()) {
            System.out.println("sql string: " + resq.getString("TABLE_NAME"));
            set.add(resq.getString("TABLE_NAME"));
        }
        System.out.println(set);
        if(!set.contains("users".toUpperCase())){
            statement.execute(CREATE_USERS_TABLE_DDL_SQL);
        }
        statement.execute(sql);
        Collection<User> all = getAll();
        for (User user:all){
            System.out.println(user.toString());

        }
        statement.close();

        return true;
    }

    private static String mapColumnLabel(String fieldName) {
        return fieldName;
    }
    private Object executeQuerySql(String querySql, String returnType) throws SQLException, ClassNotFoundException, IllegalAccessException, IntrospectionException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        List<Object> result = new ArrayList<>();

        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet res = statement.executeQuery(querySql);
        BeanInfo userBeanInfo = Introspector.getBeanInfo(Class.forName(returnType), Object.class);

        while (res.next()) {
            Object object = Class.forName(returnType).newInstance();
            for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                String fieldName = propertyDescriptor.getName();
                Class fieldType = propertyDescriptor.getPropertyType();
                String methodName = resultSetMethodMappings.get(fieldType);
                String columnLabel = fieldName;
                Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                Object resultValue = resultSetMethod.invoke(res, columnLabel);
                Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                setterMethodFromUser.invoke(object, resultValue);
            }
            result.add(object);
            System.out.println(object.toString());
        }
        statement.close();
        return result;
    }
    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");
        preparedStatementMethodMappings.put(Long.class, "setLong");
        preparedStatementMethodMappings.put(String.class, "setString");
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation: annotations) {
            if (annotation instanceof Insert) {
                try {
                    return executeInsertSql(((Insert) annotation).value(), args);
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if (annotation instanceof Select) {
                try {
                    return executeQuerySql(((Select) annotation).value(), ((Select) annotation).returnType());
                } catch (SQLException | ClassNotFoundException | IllegalAccessException | IntrospectionException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
