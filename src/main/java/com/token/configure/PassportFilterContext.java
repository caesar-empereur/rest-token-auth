package com.token.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.token.exception.ParameterException;

/**
 * Created by Administrator on 2018/6/2.
 * 通行证策略调用类的上下文
 */
public class PassportFilterContext {
    
    private static final Map<PassportStrategy, Filter> STRATEGY_FILTER_MAP = new HashMap<>();
    
    public PassportFilterContext withFilter(Filter filter) {
        if (filter instanceof LoginProcessor) {
            STRATEGY_FILTER_MAP.put(PassportStrategy.LOGIN, filter);
        }
        else if (filter instanceof LogoutProcessor) {
            STRATEGY_FILTER_MAP.put(PassportStrategy.LOGOUT, filter);
        }
        else {
            throw new ParameterException("incorrect filter");
        }
        return this;
    }
    
    /**
     * 根据登入登出的不同操作, 选用不同的策略类调用
     */
    public void filter(HttpServletRequest request,
                       HttpServletResponse response,
                       FilterChain filterChain,
                       PassportStrategy strategy) {
        Filter filter = STRATEGY_FILTER_MAP.get(strategy);
        if (filter == null) {
            return;
        }
        try {
            filter.doFilter(request, response, filterChain);
        }
        catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
