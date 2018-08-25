package com.app.core;

/**
 * Created by Administrator on 2018/5/29.
 *  需要实现的根据用户名查找用户的接口
 */
@FunctionalInterface
public interface UserProvider<U extends User> {

    U findByUsername(String username);
}
