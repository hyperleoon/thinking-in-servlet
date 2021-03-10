package com.hyperleon.research.web.app.controller;

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
    public String execute(HttpServletRequest request, HttpServletResponse response) {
//        String password = request.getParameter("password");
//        String email = request.getParameter("email");
//        Collection<User> all = userService.();
//        for (User userTmp:all){
//            if (userTmp.getEmail().equals(email) && userTmp.getPassword().equals(password)){
//                return "success.jsp";
//            }
//        }
        return "failed.jsp";
    }
}
