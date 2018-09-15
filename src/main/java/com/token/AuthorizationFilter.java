package com.token;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.token.configure.PathToRoleMappingAdapter;
import com.token.core.AuthorizationStrategy;
import com.token.core.User;
import com.token.exception.TokenInvalidException;
import com.token.exception.TokenNullException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pc on 2018/3/23.
 */
public class AuthorizationFilter extends PassportFilter {
    
    private static final Log log = LogFactory.getLog(AuthorizationFilter.class);
    
    private static final String TOKEN_KEY = "token";
    
    private static final HttpStatus UNAUTHORIZED_STATUS = HttpStatus.UNAUTHORIZED;
    
    private PathToRoleMappingAdapter mappingAdapter;
    
    public AuthorizationFilter setMappingAdapter(PathToRoleMappingAdapter mappingAdapter) {
        Assert.notNull(mappingAdapter, "PathToRoleMappingAdapter can not be null");
        this.mappingAdapter = mappingAdapter;
        return this;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        log.info("权限过滤:" + mappingAdapter.getPathToRoleMatchers());
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies == null) {
                httpServletResponse.setStatus(UNAUTHORIZED_STATUS.value());
                fireException(new TokenNullException());
                return;
            }
            for (Cookie cookie : cookies) {
                if ("auth".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
            if (StringUtils.isBlank(token)) {
                httpServletResponse.setStatus(UNAUTHORIZED_STATUS.value());
                fireException(new TokenNullException());
                return;
            }
        }
        
        User user = tokenProvider.findByToken(token);
        if (user == null || mappingAdapter.filterPassing(httpServletRequest,
                                                         user) == AuthorizationStrategy.DENY) {
            httpServletResponse.setStatus(UNAUTHORIZED_STATUS.value());
            fireException(new TokenInvalidException());
            return;
        }
        SecurityContextManager.setCurrentUser(user);
        SecurityContextManager.setCurrentRequest(httpServletRequest);
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        SecurityContextManager.clearCurrentRequest();
        SecurityContextManager.clearCurrentUser();
    }
    
    private void fireException(RuntimeException exception) {
        Observable.create((ObservableEmitter<Object> emitter) -> emitter.onNext(exception))
                  .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.io())
                  .subscribe((Object o) -> {
                      log.info(exception);
                      if (exception instanceof TokenInvalidException) {
                          throw new TokenInvalidException(exception.getMessage());
                      }
                      throw new RuntimeException(exception.getMessage());
                  });
    }
    
}
