package com.app.configure;

import com.app.core.AuthorizationStrategy;
import com.app.core.PathToRoleMatcher;
import com.app.core.User;
import com.app.core.UserRole;
import com.app.exception.ParameterException;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/28.
 */
public class PathToRoleMappingAdapter {
    
    private Set<PathToRoleMatcher> pathToRoleMatchers;
    
    public PathToRoleMappingAdapter(Set<PathToRoleMatcher> pathToRoleMatchers) {
        if (CollectionUtils.isEmpty(pathToRoleMatchers)) {
            throw new ParameterException("parameter required");
        }
        this.pathToRoleMatchers = pathToRoleMatchers;
    }

    public Set<PathToRoleMatcher> getPathToRoleMatchers() {
        return pathToRoleMatchers;
    }

    /*
            根据请求对象匹配用户角色，决定在过滤器中是否通过
         */
    public AuthorizationStrategy filterPassing(HttpServletRequest request,
                                               User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return AuthorizationStrategy.EXCEED;
        }
        for (PathToRoleMatcher mapper : pathToRoleMatchers) {
            if (request.getRequestURI().contains(mapper.getUrl())) {
                if (mapper.getUserRole() == user.getRole()) {
                    return AuthorizationStrategy.PASS;
                }
            }
        }
        return AuthorizationStrategy.DENY;
    }
}
