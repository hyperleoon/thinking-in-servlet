package com.hyperleon.research.web.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * forward to jsp
 * @author leon
 * @date 2021-03-03 08:46
 **/
public interface PageController extends Controller{

    /**
     * execute web request,forward to jsp servlet
     * @param request web request
     * @param response web response
     * @return jsp path
     * @throws Throwable exception
     */
    String execute (HttpServletRequest request, HttpServletResponse response) throws Throwable;
}
