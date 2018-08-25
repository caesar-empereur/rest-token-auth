package com.app.annotion;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by pc on 2018/4/2.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TokenInitialization.class)
@Documented
public @interface EnableAuthorizationToken {
}
