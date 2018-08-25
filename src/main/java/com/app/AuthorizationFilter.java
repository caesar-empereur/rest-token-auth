package com.app;

import com.app.configure.PathToRoleMappingAdapter;
import com.app.core.AuthorizationStrategy;
import com.app.core.TokenProvider;
import com.app.core.User;
import com.app.exception.ParameterException;
import com.app.exception.TokenInvalidException;
import com.app.exception.TokenNullException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by pc on 2018/3/23.
 */
public class AuthorizationFilter implements Filter {
    
    private static final Log log = LogFactory.getLog(AuthorizationFilter.class);
    
    private static final String TOKEN_KEY = "token";
    
    private static final HttpStatus UNAUTHORIZED_STATUS = HttpStatus.UNAUTHORIZED;
    
    private PathToRoleMappingAdapter mappingAdapter;
    
    private TokenProvider tokenProvider;
    
    public AuthorizationFilter setMappingAdapter(PathToRoleMappingAdapter mappingAdapter) {
        if (mappingAdapter == null) {
            throw new ParameterException("PathToRoleMappingAdapter can not be null");
        }
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
                      throw new RuntimeException(exception);
                  });
    }

}
