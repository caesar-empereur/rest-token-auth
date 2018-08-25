package com.app.core;

/**
 * 过滤器权限检查结果
 */
public enum AuthorizationStrategy {
                                   PASS,    //通过
                                   DENY,    //拒绝
                                   EXCEED;  //角色是管理员则任何接口都可以访问

}
