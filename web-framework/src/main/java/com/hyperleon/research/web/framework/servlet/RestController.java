package com.hyperleon.research.web.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * direct return json
 * @author leon
 * @date 2021-03-03 08:46
 **/
public interface RestController extends Controller {

    /**
     * execute web request,direct write json string to client
     * @param request web request
     * @param response web response
     * @return jsp path
     * @throws Throwable exception
     */
    String execute (HttpServletRequest request, HttpServletResponse response) throws Throwable;
}
