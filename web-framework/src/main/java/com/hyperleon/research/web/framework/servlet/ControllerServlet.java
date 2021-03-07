package com.hyperleon.research.web.framework.servlet;

import org.apache.commons.lang.StringUtils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * controller servlet proxy
 * @author leon
 * @date 2021-03-03 08:14
 **/
public class ControllerServlet extends HttpServlet {

    private final Map<String, Controller> controllersMapping = new HashMap<>();

    private final Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    @Override
    public void init(ServletConfig servletConfig) {
        servletConfig.getServletContext().log("init");
        initHandleMethods();
    }

    private void initHandleMethods() {
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<?> controllerClass = controller.getClass();
            Path pathFromClass = controllerClass.getAnnotation(Path.class);
            String requestPath = pathFromClass.value();
            Method[] publicMethods = controllerClass.getMethods();
            for (Method method : publicMethods) {
                if (!method.isAnnotationPresent(Path.class)) {
                    continue;
                }
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path pathFromMethod = method.getAnnotation(Path.class);
                if (pathFromMethod != null) {
                    requestPath += pathFromMethod.value();
                }
                HandlerMethodInfo handlerMethodInfo = new HandlerMethodInfo(requestPath, method, supportedHttpMethods);
                List<Class> parameterTypesLists = new ArrayList<>();
                for (Parameter parameter:method.getParameters()) {
                    if (parameter.isAnnotationPresent(QueryParam.class)) {
                        parameterTypesLists.add(parameter.getType());
                    }
                }
                handlerMethodInfo.setParameterTypes(parameterTypesLists);
                handleMethodInfoMapping.put(requestPath,handlerMethodInfo);
            }
            controllersMapping.put(requestPath, controller);
        }
    }

    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotationFromMethod : method.getAnnotations()) {
            HttpMethod httpMethod = annotationFromMethod.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }
        return supportedHttpMethods;
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String preFixPath = request.getContextPath();
        String requestMappingPath = substringAfter(requestUri,
                StringUtils.replace(preFixPath, "//", "/"));
        if (requestMappingPath.endsWith("/")) {
            requestMappingPath = requestMappingPath.substring(0, requestMappingPath.length() - 1);
        }
        Controller controller = controllersMapping.get(requestMappingPath);
        if (controller != null) {
            controller.init(request, response);
            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try {
                if (handlerMethodInfo != null) {
                    String httpMethod = request.getMethod();
                    if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    Map<String, String[]> parameterMap = request.getParameterMap();
                    String[] invokeArgs = new String[parameterMap.size()];
                    Method handlerMethod = handlerMethodInfo.getHandlerMethod();
                    if (handlerMethod.isAnnotationPresent(Jsp.class)) {
                        if (handlerMethod.isAnnotationPresent(GET.class)) {
                            handleJspGet(request, response, invokeArgs, handlerMethod, parameterMap, controller, handlerMethodInfo);
                            return;
                        }
                        if (handlerMethod.isAnnotationPresent(POST.class)) {
                            handlerJspPost(request, response, invokeArgs, handlerMethod, parameterMap, controller, handlerMethodInfo);
                        }
                    } else {
                        //todo rest
                        String content = (String) handlerMethod.invoke(controller, invokeArgs);
                        response.getWriter().write(content);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            } catch(Throwable throwable){
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            } finally{
                controller.clear();
            }
        }
    }

    private void handlerJspPost(HttpServletRequest request, HttpServletResponse response, String[] invokeArgs, Method handlerMethod, Map<String, String[]> parameterMap, Controller controller, HandlerMethodInfo handlerMethodInfo)
            throws Exception {


    }

    private void handleJspGet(HttpServletRequest request, HttpServletResponse response, String[] invokeArgs, Method handlerMethod, Map<String, String[]> parameterMap,Controller controller,HandlerMethodInfo handlerMethodInfo)
            throws Exception {
        Parameter[] parameters = handlerMethod.getParameters();
        if (parameterMap != null && parameterMap.size() > 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(QueryParam.class)) {
                    QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
                    if (queryParam != null) {
                        invokeArgs[i] = parameterMap.getOrDefault(queryParam.value(),null)[0];
                    } else {
                        invokeArgs[i] = null;
                    }

                }
            }
        }
        String viewPath = (String) handlerMethod.invoke(controller,handleArgsType(handlerMethodInfo.getParameterTypes(),invokeArgs));
        ServletContext servletContext = request.getServletContext();
        if (!viewPath.startsWith("/")) {
            viewPath = "/" + viewPath;
        }
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
        //forward to jsp servlet
        requestDispatcher.forward(request, response);

    }



    private Object[] handleArgsType(List<Class> parameterTypes, String[] invokeArgs) {
        if (invokeArgs == null || invokeArgs.length == 0) {
            Object[] empty = new Object[parameterTypes.size()];
            for (int i = 0; i< parameterTypes.size();i++) {
                empty[i] = null;
            }
            return empty;
        }
        Object[] finalInvokeAges = new Object[invokeArgs.length];
        for (int i = 0;i < invokeArgs.length; i++) {
            Class<?> parameterType = parameterTypes.get(i);
            Object finalInvokeAge;
            String invokeArg = invokeArgs[i];
            if (parameterType.isAssignableFrom(Long.class)) {
                finalInvokeAge = Long.parseLong(invokeArg);
            } else if (parameterType.isAssignableFrom(Integer.class)) {
                finalInvokeAge = Integer.parseInt(invokeArg);
            } else if (parameterType.isAssignableFrom(Double.class)) {
                finalInvokeAge = Double.valueOf(invokeArg);
            } else if (parameterType.isAssignableFrom(Float.class)) {
                finalInvokeAge = Float.valueOf(invokeArg);
            } else if (parameterType.isAssignableFrom(Boolean.class)) {
                if ("true".equals(invokeArg)) {
                    finalInvokeAge = Boolean.TRUE;
                } else {
                    finalInvokeAge = Boolean.FALSE;
                }
            } else if (parameterType.isAssignableFrom(String.class)) {
                finalInvokeAge = invokeArg;
            } else {
                throw new RuntimeException("get request not support this paramter type");
            }
            finalInvokeAges[i] = finalInvokeAge;
        }
        return finalInvokeAges;
    }

}
