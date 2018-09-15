package com.token.core;

import java.io.Serializable;

/**
 * 使用这个权限认证的用户对象必须实现这个接口
 */
public interface User extends Serializable {
    
    String getId();
    
    String getUsername();
    
    String getPassword();
    
    UserRole getRole();
}
