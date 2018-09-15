package com.token;

import java.io.IOException;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.token.configure.LogoutProcessor;
import com.token.configure.PassportFilterContext;
import com.token.core.PasswordEncode;
import com.token.configure.LoginProcessor;
import com.token.core.UserProvider;
import com.token.configure.PassportEndpointMatcher;
import com.token.core.TokenProvider;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Created by Administrator on 2018/6/2.
 * 本类是处理登入登出操作的总入口，因此称为通行证 登入登出操作都是基于过滤器的
 */
public class PassportFilter implements Filter {
    
    protected static UserProvider userProvider;
    
    protected static TokenProvider tokenProvider;
    
    protected static PasswordEncode passwordEncode;
    
    /**
     * 策略模式的上下文
     */
    private PassportFilterContext filterContext = new PassportFilterContext();
    
    private Set<PassportEndpointMatcher> endpointMatchers;
    
    public PassportFilter withEndpointMatchers(Set<PassportEndpointMatcher> endpointMatchers) {
        this.endpointMatchers = endpointMatchers;
        return this;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        /**
         * 因为filter 的初始化发生在 spring 容器初始化的前面, 因此用这种方式获取到 bean
         */
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext webApplicationContext =
                                                    WebApplicationContextUtils.getWebApplicationContext(servletContext);
        userProvider = webApplicationContext.getBean(UserProvider.class);
        tokenProvider = webApplicationContext.getBean(TokenProvider.class);
        passwordEncode = webApplicationContext.getBean(PasswordEncode.class);
        filterContext.withFilter(new LoginProcessor()).withFilter(new LogoutProcessor());
    }
    
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        for (PassportEndpointMatcher matcher : endpointMatchers) {
            if (httpServletRequest.getRequestURI().contains(matcher.getUrl())) {
                filterContext.filter(httpServletRequest,
                                     httpServletResponse,
                                     chain,
                                     matcher.getStrategy());
            }
        }
    }
    
    @Override
    public void destroy() {
        
    }
}
