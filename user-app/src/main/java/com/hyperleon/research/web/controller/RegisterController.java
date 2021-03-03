package com.hyperleon.research.web.controller;

import com.hyperleon.research.controller.PageController;
import com.hyperleon.research.web.domain.User;
import com.hyperleon.research.web.service.UserService;
import com.hyperleon.research.web.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author leon
 * @date 2021-03-03 19:13
 **/
@Path("/login")
public class RegisterController implements PageController {

    private UserService userService = new UserServiceImpl();

    @GET
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
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
