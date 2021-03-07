package com.hyperleon.research.web.framework.servlet;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * wrapper of handle method info
 * @author leon
 * @date 2021-03-03 08:46
 */
public class HandlerMethodInfo {

    private final String requestPath;

    private final Method handlerMethod;

    private final Set<String> supportedHttpMethods;

    private List<Class> parameterTypes;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportedHttpMethods) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public List<Class> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<Class> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
