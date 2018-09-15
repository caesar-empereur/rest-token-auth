package com.token.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.token.PassportFilter;
import com.token.core.User;
import com.token.exception.TokenInvalidException;
import com.token.exception.ParameterException;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Administrator on 2018/5/29.
 */
public class LoginProcessor extends PassportFilter {
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String USERNAME_PARAMETER = "username";
    
    private static final String PASSWORD_PARAMETER = "password";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
    
    @Override
    public void destroy() {
        
    }

    /**
        这里起到controller 的作用, 拦截用户的登陆请求,
        然后直接修改 response 生成登陆结果, 过滤器不用过
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String username = httpServletRequest.getParameter(USERNAME_PARAMETER);
        String password = httpServletRequest.getParameter(PASSWORD_PARAMETER);
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ParameterException("username or password missing");
        }
        User user = userProvider.findByUsername(username);
        if (user == null) {
            throw new TokenInvalidException("invalid app");
        }
        if (passwordEncode.matches(password, user.getPassword())) {
            throw new ParameterException("password incorrect");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenProvider.saveToken(token, user);
        
        httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpServletResponse.getWriter()
                           .write(objectMapper.writeValueAsString(new LoginResponse(true, token)));
    }
    
    static class LoginResponse extends HashMap<String, Object> {
        
        public LoginResponse(boolean succeed, String token) {
            put("succeed", succeed);
            put("token", token);
        }
    }
}
