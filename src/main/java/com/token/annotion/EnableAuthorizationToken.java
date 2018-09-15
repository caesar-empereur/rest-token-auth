package com.token.annotion;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * Created by pc on 2018/4/2.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TokenInitialization.class)
@Documented
public @interface EnableAuthorizationToken {
}
