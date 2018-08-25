package com.app.core;

/**
 * Created by Administrator on 2018/5/22.
 * 使用这个权限认证模块必须要实现的处理 app 的接口
 */
public interface TokenProvider<U extends User> {

    void saveToken(String token, U user);
    
    void removeToken(String token);
    
    U findByToken(String token);
}
