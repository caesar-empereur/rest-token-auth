## 说明文档

* 功能说明
```
这是一个基于 springboot:2.0.0.M7版本的权限认证的模块
使用该模块编译后的jar包可实现基本的管理员普通用户权限过滤
```
* 工程需要的环境
```
gradle4.3, JDK1.8
```
* 代码check下来后编译jar包的方式
```
进入工程的根目录，也就是gradle.build的目录, 执行 gradle clean jar命令
```

### 1 关于权限认证为什么不用 shiro, spring-security 的说明
```
shiro, spring-security 需要配置权限认证错误的跳转页面,需要客户端是浏览器的
环境。但是移动端都是 http rest 请求, 要达到这种效果需要拿到返回的response
的 html 页面方法 web-view 中展示,真正的前后端分离可以直接返回 401 未授权
状态码，让客户端自己控制页面展示
```

### 2 目前实现的权限过滤功能
* 保存的用户对象有角色信息，包括管理员和普通用户, 管理员拥有一切权限
* 根据请求的 url 来匹配用户角色

### 3 使用该模块进行权限过滤需要的初始化操作
* 配置servlet filter ,配置过滤器需要拦截的 url 与 该 url 需要的用户权限, 操作如下
```
    @Bean
    public PathToRoleMappingAdapter pathToRoleMappingAdapter() {
        Set<PathToRoleMatcher> mappers = new HashSet<>();
        mappers.add(new PathToRoleMatcher("home", UserRole.USER));
        mappers.add(new PathToRoleMatcher("admin", UserRole.ADMIN));
        return new PathToRoleMappingAdapter(mappers);
    }
```
* 配置登入登出的 url 路径, 操作如下
```
      @Bean
      public PassportEndpointMatcherAdapter passportEndpointMatcherAdapter() {
          Set<PassportEndpointMatcher> matchers = new HashSet<>();
          matchers.add(new PassportEndpointMatcher("login", PassportStrategy.LOGIN));
          matchers.add(new PassportEndpointMatcher("logout", PassportStrategy.LOGOUT));
          return new PassportEndpointMatcherAdapter(matchers);
      }
```
* 实现 3 个接口
```
  UserProvider 登录操作需要检查用户是否存在
  TokenProvider 过滤器中需要删除或者根据用户的token去查询是否有效
  PasswordEncode 登入中需要拿到请求中的密码,加密后跟数据库保存的加密密码匹配密码是都有效
  
  auth 模块的正常使用需要这 3 个接口有实现类，就是把具体的逻辑交给使用者去实现
```
* springboot启动类配置 
```
@ComponentScan({ "com.app", "com.token" })
除了自己模块的包名外，还要加上auth 模块的包 扫描
```
### 4 权限过滤流程
* 用户表单登录,参数为用户名,密码,登录成功则返回 token，失败则看具体信息
* 登录成功后请求 资源受保护的接口时,需要把 token 放到请求的 header 中, key 为 "token"，value 就是token的值
* 权限认证成功则可以进入到 controller, 失败则返回 http 状态码 401, 表示未授权
