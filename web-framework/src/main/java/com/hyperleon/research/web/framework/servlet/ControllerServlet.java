package com.hyperleon.research.web.framework.servlet;

import com.hyperleon.research.web.context.ComponentContext;
import org.apache.commons.lang.StringUtils;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
        initHandleMethods(servletConfig.getServletContext());
    }

    private void initHandleMethods(ServletContext servletContext) {
        ComponentContext componentContext = (ComponentContext)servletContext.getAttribute(ComponentContext.CONTEXT_NAME);
        List<Object> componentInstances = componentContext.getComponentInstance();
        for (Object object: componentInstances) {
            if (object instanceof Controller) {
                Controller controller = (Controller) object;
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
                    handleMethodInfoMapping.put(requestPath,
                            new HandlerMethodInfo(requestPath, method, supportedHttpMethods));
                }
                controllersMapping.put(requestPath, controller);
            }
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
        String requestURI = request.getRequestURI();
        String servletContextPath = request.getContextPath();
        String prefixPath = servletContextPath;
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null) {
            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
            try {
                if (handlerMethodInfo != null) {
                    String httpMethod = request.getMethod();
                    if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    if (controller instanceof PageController) {
                        PageController pageController = (PageController) controller;
                        String viewPath = pageController.execute(request, response);
                        ServletContext servletContext = request.getServletContext();
                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(request, response);
                        return;
                    } else if (controller instanceof RestController) {
                        RestController restController = (RestController) controller;
                        String content = restController.execute(request, response);
                        response.getWriter().write(content);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }

                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
        }
    }

}
