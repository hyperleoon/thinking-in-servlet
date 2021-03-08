package com.hyperleon.research.web.app.controller.demo;

import com.hyperleon.research.web.framework.servlet.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author leon
 * @date 2021-03-03 10:20
 **/
@Path("/page")
public class DefaultPageController implements PageController {

    @GET
    @Path("/hello-world")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }
}
