package com.hyperleon.research.web.app.controller;

import com.hyperleon.research.web.framework.servlet.AbstractController;
import com.hyperleon.research.web.framework.servlet.Jsp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author leon
 * @date 2021-03-03 10:20
 **/
@Path("/page")
public class DefaultPageController extends AbstractController {

    @GET
    @Path("/hello-world")
    @Jsp
    public String helloWorld(@QueryParam("name") String name,@QueryParam("id") Long id) {
        System.out.println(name+id);
        return "index.jsp";
    }

}
