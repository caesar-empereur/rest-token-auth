package com.app;

import com.app.core.User;
import com.app.exception.ParameterException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yang on 2018/5/20.
 */
public class SecurityContextManager {
    
    private SecurityContextManager() {
    }
    
    private static final ThreadLocal<HttpServletRequest> CURRENT_REQUEST = new ThreadLocal<>();
    
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();
    
    public static void clearCurrentRequest() {
        CURRENT_REQUEST.remove();
    }
    
    public static void setCurrentRequest(HttpServletRequest request) {
        if (request == null) {
            throw new ParameterException("request must not be null");
        }
        CURRENT_REQUEST.set(request);
    }
    
    public static HttpServletRequest getCurrentRequest() {
        if (CURRENT_REQUEST.get() == null) {
            throw new RuntimeException("current request is null");
        }
        return CURRENT_REQUEST.get();
    }
    
    public static void setCurrentUser(User user) {
        if (user == null) {
            throw new ParameterException("user must not be null");
        }
        CURRENT_USER.set(user);
    }
    
    public static User getCurrentUser() {
        if (CURRENT_USER.get() == null) {
            throw new RuntimeException(" current user is null");
        }
        return CURRENT_USER.get();
    }
    
    public static void clearCurrentUser() {
        CURRENT_USER.remove();
    }
}
