package com.app.configure;

import java.util.Set;

/**
 * @author yingyang
 * @date 2018/6/9.
 */
public class PassportEndpointMatcherAdapter {
    
    private Set<PassportEndpointMatcher> passportEndpointMatchers;
    
    public PassportEndpointMatcherAdapter(Set<PassportEndpointMatcher> passportEndpointMatchers) {
        this.passportEndpointMatchers = passportEndpointMatchers;
    }

    public Set<PassportEndpointMatcher> getPassportEndpointMatchers() {
        return passportEndpointMatchers;
    }
}
