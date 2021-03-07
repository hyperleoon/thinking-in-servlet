package com.hyperleon.research.web.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * abstract implement
 * @author leon
 * @date 2021-03-07 16:48
 **/
public abstract class AbstractController implements Controller {

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void clear() {
        this.request = null;
        this.response = null;
    }
}
