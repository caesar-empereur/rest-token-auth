package com.token.configure;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import com.token.PassportFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2018/5/31.
 */
public class LogoutProcessor extends PassportFilter {
    
    private static final String TOKEN_KEY = "token";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 登出操作是为了删除用户缓存的 app
     */
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException,
                                            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(TOKEN_KEY);
        if (StringUtils.isNotBlank(token)) {
            tokenProvider.removeToken(token);
        }
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        
    }
}
