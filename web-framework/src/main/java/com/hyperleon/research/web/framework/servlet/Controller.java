package com.hyperleon.research.web.framework.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A tagging interface
 * @author leon
 * @date 2021-03-03 08:45
 **/
public interface Controller {

    void init(HttpServletRequest request,HttpServletResponse response);

    void clear();
}
