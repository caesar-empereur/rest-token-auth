package com.app.annotion;

import com.app.AuthorizationFilter;
import com.app.PassportFilter;
import com.app.configure.PassportEndpointMatcher;
import com.app.configure.PassportEndpointMatcherAdapter;
import com.app.configure.PathToRoleMappingAdapter;
import com.app.core.PathToRoleMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by pc on 2018/4/2.
 */
@Configuration
public class TokenInitialization implements ImportBeanDefinitionRegistrar, WebMvcConfigurer {
    
    protected AnnotationAttributes enableToken;
    
    private static final Log log = LogFactory.getLog(TokenInitialization.class);
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        this.enableToken =
                         AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableAuthorizationToken.class.getName(),
                                                                                                     false));
    }
    
    @SuppressWarnings("all")
    @Bean
    public FilterRegistrationBean filterRegistrationBeanAuthorization(PathToRoleMappingAdapter mappingAdapter) {
        Assert.notNull(mappingAdapter, "missing bean PathToRoleMappingAdapter");
        Set<PathToRoleMatcher> mappers = mappingAdapter.getPathToRoleMatchers();
        Set<String> urlPatterns = new LinkedHashSet<>();
        for (PathToRoleMatcher mapper : mappers) {
            urlPatterns.add(String.format("/%s/*", mapper.getUrl()));
        }
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AuthorizationFilter().setMappingAdapter(mappingAdapter));
        registrationBean.setName("authorizationFilter");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
    
    @SuppressWarnings("all")
    @Bean
    public FilterRegistrationBean filterRegistrationBeanPassport(PassportEndpointMatcherAdapter matcherAdapter) {
        Assert.notNull(matcherAdapter, "missing bean PassportEndpointMatcherAdapter");
        Set<PassportEndpointMatcher> endpointMatchers =
                                                      matcherAdapter.getPassportEndpointMatchers();
        Set<String> urlPatterns = new LinkedHashSet<>();
        for (PassportEndpointMatcher matcher : endpointMatchers) {
            urlPatterns.add(String.format("/%s/*", matcher.getUrl()));
        }
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new PassportFilter().withEndpointMatchers(endpointMatchers));
        registrationBean.setName("passportFilter");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
}
