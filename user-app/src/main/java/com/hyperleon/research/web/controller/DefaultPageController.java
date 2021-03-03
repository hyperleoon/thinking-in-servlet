package com.hyperleon.research.web.controller;

import com.hyperleon.research.controller.PageController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author leon
 * @date 2021-03-03 10:20
 **/
@Path("/page/hello")
public class DefaultPageController implements PageController {

    @GET
    @Path("/world")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        return "index.jsp";
    }

}
