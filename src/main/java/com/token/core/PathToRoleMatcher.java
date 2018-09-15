package com.token.core;

/**
 * Created by Administrator on 2018/5/28.
 * 该对象保存过滤器需要拦截的 url 与该 url 需要的用户权限的对应关系
 */
public class PathToRoleMatcher {

    private String url;

    private UserRole userRole;

    public PathToRoleMatcher(String url, UserRole userRole) {
        this.url = url;
        this.userRole = userRole;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
