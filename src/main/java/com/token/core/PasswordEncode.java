package com.token.core;

/**
 * Created by Administrator on 2018/5/31.
 * 使用这个权限认证模块必须要实现的 密码加密接口
 */
public interface PasswordEncode {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
