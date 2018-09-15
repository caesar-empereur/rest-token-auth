package com.token.configure;

/**
 * Created by Administrator on 2018/6/2.
 * 该类用来保存需要拦截的 url 跟这个 url
 * 需要的用户角色的对应关系, 用于登入登出操作
 * 与访问接口的过滤器区分开
 */
public class PassportEndpointMatcher {

    private String url;

    private PassportStrategy strategy;

    public PassportEndpointMatcher(String url, PassportStrategy strategy) {
        this.url = url;
        this.strategy = strategy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PassportStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PassportStrategy strategy) {
        this.strategy = strategy;
    }
}
