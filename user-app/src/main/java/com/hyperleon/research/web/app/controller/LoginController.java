package com.hyperleon.research.web.app.controller;

import com.hyperleon.research.web.app.domain.User;
import com.hyperleon.research.web.app.service.UserService;
import com.hyperleon.research.web.framework.servlet.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author leon
 * @date 2021-03-08 22:19
 **/
@Path("/login")
public class LoginController implements PageController {

    @Resource(name = "bean/UserService")
    public UserService userService;

    @POST
    @Override
    @Path("")
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        User target = userService.queryUserByNameAndPassword(name,password);
        if (target == null) {
            return "fail.jsp";
        }
        return "success.jsp";
    }
}
