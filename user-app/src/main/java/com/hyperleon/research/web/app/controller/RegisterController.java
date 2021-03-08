package com.hyperleon.research.web.app.controller;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.app.service.UserService;
import com.hyperleon.research.web.app.service.impl.UserServiceImpl;
import com.hyperleon.research.web.framework.servlet.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author leon
 * @date 2021-03-03 19:13
 **/
@Path("/register")
public class RegisterController implements PageController {

    private UserService userService = new UserServiceImpl();

    @Override
    @POST
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String user = request.getParameter("user");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        if (user == null || password == null) {
            return "register.jsp";
        }
        if (userService.register(new User(user, password, email, phoneNumber))) {
            return "login-form.jsp";
        }
        return "failed.jsp";
    }

}

