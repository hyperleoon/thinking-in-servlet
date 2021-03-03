package com.hyperleon.research.web.controller;

import com.hyperleon.research.controller.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author leon
 * @date 2021-03-03 10:23
 **/
@Path("/rest/hello")
public class DefaultRestController implements RestController {

    @GET
    @Path("/world")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        return "666";
    }
}
