# server-security

> 本项目是基于`Spring Security`实现的前后端分离项目，希望能提供一些前后端分离的解决方案给使用此框架（`Spring Security`）的开发者。

## `Security`架构

对基于`Servlet`的应用，`Spring Security`是通过其`Filter`（过滤器）来实现的。在应用程序接收到`Servlet`请求后，容器就会创建一个`FilterChain`（包含`Filter`和`Servlet`）来进行处理，其中`Filter`（过滤器）可以阻止请求进一步向下执行或者对请求的参数/返回值进行修改，`Servlet`则会在所有`Filter`通过后进行业务处理。

虽然`Servlet`容器允许以它标准的方式来注册`Filter`，但是这样却不能识别到`Spring`所定义的`Bean`。为了实现这一点，`Spring`提供了一个`Filter`实现类`DelegatingFilterProxy`，它会将`Servlet`容器的生命周期与`Spring`的`ApplicationContext`桥接在一起，即`DelegatingFilterProxy`可以通过`Servlet`容器标准的方式进行注册，同时也能够将所有的工作委托给`Spring`中实现了`Filter`的`Bean`。

因此，在经过`Spring Security`的一系列封装改造后，它的整体架构演变成下面这样：

```text
                                                                          SecurityFilterChain0
                                                                      +----------------------------+
                                                                      |          /api/**           |
                                                                      |    +------------------+    |
                                                                      |    | Security Filter0 |    |
                                                                      |    +--------+---------+    |
                                                                      |             |              |
              +----------+                           +---------------->         +--------+         |
              |  Client  |                           |                |         |--------|         |
              +----+-----+                           |                |         |--------|         |
 FilterCain        |                                 |                |         |--------|         |
+---------------------------------------+            |                |         |--------|         |
|        +---------v-----------+        |            |                |         +--------+         |
|        |       Filter0       |        |            |                |             |              |
|        +---------+-----------+        |            |                |    +--------v---------+    |
|                  |                    |            |                |    | Security FilterN |    |
|                  |                    |            |                |    +------------------+    |
|   +--------------v----------------+   |            |                |                            |
|   |     DelegatingFilterProxy     |   |       +----+-----+          +----------------------------+
|   | +---------------------------+ |   |       |          |
|   | |      FilterChainProxy     |------------>|  select? |              +--------------------+
|   | +---------------------------+ |   |       |          |              |                    |
|   +-------------------------------+   |       +----+-----+              +--------------------+
|                  |                    |            |                 
|                  |                    |            |                    +--------------------+
|        +---------v-----------+        |            |                    |                    |
|        |       FilterN       |        |            |                    +--------------------+
|        +---------+-----------+        |            |
|                  |                    |            |                    +--------------------+
|                  |                    |            |                    |                    |
|        +---------v-----------+        |            |                    +--------------------+
|        |       Servlet       |        |            |
|        +---------------------+        |            |                     SecurityFilterChainN
+---------------------------------------+            |                +----------------------------+
                                                     |                |           /**              |
                                                     |                |                            |
                                                     |                |         +--------+         |
                                                     |                |         +--------+         |
                                                     |                |             |              |
                                                     +---------------->         +--------+         |
                                                                      |         +--------+         |
                                                                      |             |              |
                                                                      |         +--------+         |
                                                                      |         +--------+         |
                                                                      +----------------------------+
```

## `Security`核心组件

关于`Spring Security`的“身份认证”和“权限校验”，在`FilterChain`过滤器链中首先通过身份认证的`Security Filter`进行判断，并将认证信息存放到`SecurityContext`中，然后在后面权限校验的`Security Filter`中从`SecurityContext`中获取认证信息进行权限校验。

### `Security`身份认证

对于`Authentication`身份认证，`Spring Secutity`提供了很多预设的解决方案，其中最常用的一种就是通过`username`/`password`的方式进行认证，它们在实现上主要分为“身份认证”的处理方式和信息存储两个模块，即：

1. 对于“身份认证”的处理方式，`Spring Security`主要提供了三种类型，分别是`Form Login`、`Basic Authentication`和`Digest Authentication`。
2. 对于“身份认证”的信息存储，`Spring Security`主要提供了四种类型，分别是`In-Memory`存储、`JDBC`存储、自定义`UserDetailsService`存储和`LDAP`存储。

根据`Spring Security`的设计理念，这种类型的身份认证（`username`/`password`的方式）主要作用于登陆接口（首次登陆）。但是，如果我们通过这种方式实现登陆，就必然会加大对其开发的难度和压力。因此，笔者建议只通过`Spring Security`实现接口的认证/权限拦截，而要达到这种效果可以通过`3`种方式实现，即：

- 通过实现`AbstractAuthenticationProcessingFilter`抽象类。
- 通过实现`AbstractPreAuthenticatedProcessingFilter`抽象类。
- 通过实现`GenericFilterBean`抽象类、`OncePerRequestFilter`抽象类或者`Filter`接口。

但是`AbstractAuthenticationProcessingFilter`作为实现`UsernamePasswordAuthenticationFilter`的抽象类，它定义了（首次）登陆/授权的执行流程。显然，从这样的定义或设计理念来说，它并不适合用于实现接口的认证/权限拦截功能（不推荐）。而`GenericFilterBean`、`OncePerRequestFilter`或者`Filter`等基础类虽然是最灵活的，但是笔者认为应该在现有能力无法提供的情况下才使用它们来实现（不推荐）。而`AbstractPreAuthenticatedProcessingFilter`主要是用于处理从外部系统获取的认证，我们完全可以将登陆授权接口当作是外部系统，不进行拦截处理。这样看来`AbstractPreAuthenticatedProcessingFilter`是相当适合我们了，所以笔者主要是基于`AbstractPreAuthenticatedProcessingFilter`来实现身份认证的功能。

其中，关于`AbstractPreAuthenticatedProcessingFilter`执行流程图如下所示：

```text
                                           SecurityFilterChain                +----------------------------+
                                           +----------------------------+     |                            |
              +----------+                 |                            |     |                            |
              |  Client  |                 |    +------------------+    |     |                            v
              +----+-----+                 |    | Security Filter0 |    |     |                    +-------+--------+
 FilterCain        |                       |    +--------+---------+    |     |                    | Authentication |
+---------------------------------------+  |             |              |     |                    +-------+--------+
|        +---------v-----------+        |  |         +--------+         |     |                            |
|        |       Filter0       |        |  |         +--------+         |     |                            |
|        +---------+-----------+        |  |             |              |     |                            v
|                  |                    |  | +-----------+------------+ |     |                +-----------+-----------+
|                  |                    |  | |AbstractPreAuthenticated+-------+                | AuthenticationManager |
|   +--------------v----------------+   |  | |   ProcessingFilter     | |                      +-----------+-----------+
|   |     DelegatingFilterProxy     |   |  | +-----------+------------+ |                                  |
|   | +---------------------------+ |   |  |             |              |                                  |
|   | |      FilterChainProxy     +------->+         +--------+         |                                  v
|   | +---------------------------+ |   |  |         +--------+         |                           +------+-------+
|   +-------------------------------+   |  |             |              |                           |Authenticated?|
|                  |                    |  |  +----------v-----------+  |                           +------+-------+
|                  |                    |  |  |AbstractAuthentication|  |                                  |
|        +---------v-----------+        |  |  |  ProcessingFilter    |  |                     failure      |     success
|        |       FilterN       |        |  |  +----------+-----------+  |                 +----------------+-------------------+
|        +---------+-----------+        |  |             |              |                 |                                    |
|                  |                    |  |         +--------+         |                 |                                    |
|                  |                    |  |         +--------+         |  +--------------------------------+  +-----------------------------------+
|        +---------v-----------+        |  |             |              |  | +----------------------------+ |  | +-------------------------------+ |
|        |       Servlet       |        |  |    +--------v---------+    |  | |   SecurityContextHolder    | |  | |     SecurityContextHolder     | |
|        +---------------------+        |  |    | Security FilterN |    |  | +----------------------------+ |  | +-------------------------------+ |
+---------------------------------------+  |    +------------------+    |  | +----------------------------+ |  | +-------------------------------+ |
                                           |                            |  | |AuthenticationFailureHandler| |  | |   ApplicationEventPublisher   | |
                                           +----------------------------+  | +----------------------------+ |  | +-------------------------------+ |
                                                                           +--------------------------------+  | +-------------------------------+ |
                                                                                                               | |  AuthenticationSuccessHandler | |
                                                                                                               | +-------------------------------+ |
                                                                                                               +-----------------------------------+
```

对于`AuthenticationManager`，它最常用的实现类就是`ProviderManager`。`ProviderManager`在执行身份认证时会委托给`AuthenticationProvider`列表进行判断，在列表中的每一个`AuthenticationProvider`都有机会去判断身份认证是成功或是失败（只要适配成功），如果当前`AuthenticationProvider`无法做出判断（不适配）就会继续流向下一个`AuthenticationProvider`。但是，如果遍历整个列表都找不到适配的`AuthenticationProvider`，那么就会抛出`ProviderNotFoundException`异常（表示不支持当前类型的`Authentication`）。

> 注意，如果存在`AuthenticationProvider`适配成功后，无论是认证成功还是认证失败都不会继续适配其他`AuthenticationProvider`了。

最终，将`ProviderManager`的实现结构整合`AbstractPreAuthenticatedProcessingFilter`就演变成这样了：

```text
                                           SecurityFilterChain                +----------------------------+
                                           +----------------------------+     |                            |
              +----------+                 |                            |     |                            |
              |  Client  |                 |    +------------------+    |     |                            v
              +----+-----+                 |    | Security Filter0 |    |     |                    +-------+--------+                AuthenticationPro^iders
 FilterCain        |                       |    +--------+---------+    |     |                    | Authentication |            +-----------------------------+
+---------------------------------------+  |             |              |     |                    +-------+--------+            | +-------------------------+ |
|        +---------v-----------+        |  |         +--------+         |     |                            |                     | | AuthenticationProvider0 | |
|        |       Filter0       |        |  |         +--------+         |     |                            |                     | +------------+------------+ |
|        +---------+-----------+        |  |             |              |     |                            v                     |              |              |
|                  |                    |  | +-----------+------------+ |     |                +-----------+-----------+         |          +--------+         |
|                  |                    |  | |AbstractPreAuthenticated+-------+                |    ProviderManager    +-------->+          |--------|         |
|   +--------------v----------------+   |  | |   ProcessingFilter     | |                      +-----------+-----------+         |          |--------|         |
|   |     DelegatingFilterProxy     |   |  | +-----------+------------+ |                                  |                     |          +--------+         |
|   | +---------------------------+ |   |  |             |              |                                  |                     |              |              |
|   | |      FilterChainProxy     +------->+         +--------+         |                                  v                     | +------------v------------+ |
|   | +---------------------------+ |   |  |         +--------+         |                           +------+-------+             | | AuthenticationProvider0 | |
|   +-------------------------------+   |  |             |              |                           |Authenticated?|             | +-------------------------+ |
|                  |                    |  |  +----------v-----------+  |                           +------+-------+             +-----------------------------+
|                  |                    |  |  |AbstractAuthentication|  |                                  |
|        +---------v-----------+        |  |  |  ProcessingFilter    |  |                     failure      |     success
|        |       FilterN       |        |  |  +----------+-----------+  |                 +----------------+-------------------+
|        +---------+-----------+        |  |             |              |                 |                                    |
|                  |                    |  |         +--------+         |                 |                                    |
|                  |                    |  |         +--------+         |  +--------------------------------+  +-----------------------------------+
|        +---------v-----------+        |  |             |              |  | +----------------------------+ |  | +-------------------------------+ |
|        |       Servlet       |        |  |    +--------v---------+    |  | |   SecurityContextHolder    | |  | |     SecurityContextHolder     | |
|        +---------------------+        |  |    | Security FilterN |    |  | +----------------------------+ |  | +-------------------------------+ |
+---------------------------------------+  |    +------------------+    |  | +----------------------------+ |  | +-------------------------------+ |
                                           |                            |  | |AuthenticationFailureHandler| |  | |   ApplicationEventPublisher   | |
                                           +----------------------------+  | +----------------------------+ |  | +-------------------------------+ |
                                                                           +--------------------------------+  | +-------------------------------+ |
                                                                                                               | |  AuthenticationSuccessHandler | |
                                                                                                               | +-------------------------------+ |
                                                                                                               +-----------------------------------+
```

### `Security`权限校验

在通过了身份认证的过滤器后（例如，`AbstractAuthenticationProcessingFilter`），马上就是进一步的权限校验，即`Authorization`。在实现上，对于权限校验的处理主要涉及了`AbstractSecurityInterceptor`过滤器。这样，整个`Spring Security`的执行流程就演变成这样：

```text
                                           SecurityFilterChain
                                           +----------------------------+
                                           |                            |
                                           |    +------------------+    |
                                           |    | Security Filter0 |    |  +------------------------+
                                           |    +--------+---------+    |  |                        |
              +----------+                 |             |              |  |                        |
              |  Client  |                 |         +--------+         |  |                +-------v--------+       +-----------------------+
              +----+-----+                 |         +--------+         |  |                | Authentication +<------+ SecurityContextHolder |
 FilterCain        |                       |             |              |  |                +-------+--------+       +-----------------------+
+---------------------------------------+  |             v              |  |                        |
|        +---------v-----------+        |  | +-----------+------------+ |  |                +-------v--------+
|        |       Filter0       |        |  | |AbstractPreAuthenticated| |  |                |FilterInvocation|
|        +---------+-----------+        |  | |   ProcessingFilter     | |  |                +-------+--------+
|                  |                    |  | +-----------+------------+ |  |                        |
|                  |                    |  |             |              |  |                +-------v--------+       +-----------------------+
|   +--------------v----------------+   |  |         +--------+         |  |                |ConfigAttributes+<------+SecurityMetadataSource |
|   |     DelegatingFilterProxy     |   |  |         +--------+         |  |                +-------+--------+       +-----------------------+
|   | +---------------------------+ |   |  |             |              |  |                        |
|   | |      FilterChainProxy     +------->+  +----------v-----------+  |  |            +-----------v-----------+
|   | +---------------------------+ |   |  |  |AbstractAuthentication|  |  |            | AccessDecisionManager |
|   +-------------------------------+   |  |  |  ProcessingFilter    |  |  |            +-----------+-----------+
|                  |                    |  |  +----------+-----------+  |  |                        |
|                  |                    |  |             |              |  |                 +------v-------+
|        +---------v-----------+        |  |         +--------+         |  |                 | Authorized?  |
|        |       FilterN       |        |  |         +--------+         |  |                 +------+-------+
|        +---------+-----------+        |  |             |              |  |                        |
|                  |                    |  |  +----------v-----------+  |  |            Denied      v       success
|                  |                    |  |  |    FilterSecurity    +-----+            +-----------+-------------+
|        +---------v-----------+        |  |  |     Interceptor      |  |               |                         |
|        |       Servlet       |        |  |  +----------+-----------+  |               v                         v
|        +---------------------+        |  |             |              |  +-------------------------+ +----------+----------+
+---------------------------------------+  |             |              |  |-------------------------| | Continue Processing |
                                           |    +--------v---------+    |  || AccessDeniedException || |  Request Normally   |
                                           |    | Security FilterN |    |  |-------------------------| |                     |
                                           |    +------------------+    |  +-------------------------+ +---------------------+
                                           |                            |
                                           +----------------------------+
```

其中，`FilterSecurityInterceptor`就是以`Filter`的方式来实现权限校验的`AbstractSecurityInterceptor`实现类。除此之外，`Spring Security`为了能够更加方便地进行权限校验，其引入了权限的注解式配置（类注解/方法注解）。在实现上，它是通过`MethodSecurityInterceptor`（`AbstractSecurityInterceptor`的实现类）以动态代理的方式来实现的（不是基于过滤器`Filter`实现的）。这样，整个`Spring Security`的执行流程就演变成这样：

```text
                                           SecurityFilterChain
                                           +----------------------------+
                                           |                            |
                                           |    +------------------+    |
                                           |    | Security Filter0 |    |
                                           |    +--------+---------+    |
                                           |             |              |
                                           |         +--------+         |
                                           |         +--------+         |
                                           |             |              |
                                           |             v              |
                                           | +-----------+------------+ |
                                           | |AbstractPreAuthenticated| |
                                           | |   ProcessingFilter     | |
                                           | +-----------+------------+ |
              +----------+                 |             |              |
              |  Client  |                 |         +--------+         |
              +----+-----+                 |         +--------+         |
 FilterCain        |                       |             |              |
+---------------------------------------+  |             v              |
|                  |                    |  |  +----------+-----------+  |
|                  |                    |  |  |AbstractAuthentication|  |
|        +---------v-----------+        |  |  |  ProcessingFilter    |  |
|        |       Filter0       |        |  |  +----------+-----------+  |
|        +---------+-----------+        |  |             |              |   +-----------------------+
|                  |                    |  |         +--------+         |   | SecurityContextHolder |
|                  |                    |  |         +--------+         |   +----------+------------+
|                  |                    |  |             |              |              |
|   +--------------v----------------+   |  |  +----------v-----------+  |      +-------v--------+       +----------------+
|   |     DelegatingFilterProxy     |   |  |  |    FilterSecurity    +-------->+ Authentication +------>+FilterInvocation|
|   | +---------------------------+ |   |  |  |     Interceptor      |  |      +----------------+       +-------+--------+ +----------------------------------------------------------------------+
|   | |      FilterChainProxy     +------->+  +----------+-----------+  |                                       |          |                                                                      |
|   | +---------------------------+ |   |  |             |              |                                       |          | +-----------------------+                                            |
|   +-------------------------------+   |  |             v              |                                       |          | |SecurityMetadataSource |       AbstractSecurityInterceptor          |
|                  |                    |  |    +--------+---------+    |                                       |          | +-----------+-----------+                                            |
|                  |                    |  |    | Security FilterN |    |                                       |          |             |                                                        |
|                  |                    |  |    +------------------+    |                                       |          |     +-------v--------+     +-----------------------+                 |
|        +---------v-----------+        |  |                            |                                       +--------------->+ConfigAttributes+---->+ AccessDecisionManager |                 |
|        |       FilterN       |        |  +----------------------------+                                       |          |     +----------------+     +-----------+-----------+                 |
|        +---------+-----------+        |                                                                       |          |                                        |                             |
|                  |                    |  Spring IoC Container                                                 |          |                                 +------v-------+                     |
|                  |                    |  +----------------------------+                                       |          |                                 | Authorized?  |                     |
|                  |                    |  | MethodSecurityInterceptor  |                                       |          |                                 +------+-------+                     |
|        +---------v-----------+        |  |  +---------------------+   |                                       |          |                                        |                             |
|        |       Servlet       +---------->+  |     Proxy(AOP)      |   |      +----------------+       +-------+--------+ |                            Denied      v       success               |
|        +---------------------+        |  |  |  +---------------+  +--------->+ Authentication +------>+MethodInvocation| |                            +-----------+-------------+               |
|                                       |  |  |  |  plain object |  |   |      +-------+--------+       +----------------+ |                            |                         |               |
|                                       |  |  |  +---------------+  |   |              ^                                   |                            v                         v               |
+---------------------------------------+  |  +---------------------+   |   +----------+------------+                      |               +-------------------------+ +----------+----------+    |
                                           +----------------------------+   | SecurityContextHolder |                      |               |-------------------------| | Continue Processing |    |
                                                                            +-----------------------+                      |               || AccessDeniedException || |  Request Normally   |    |
                                                                                                                           |               |-------------------------| |                     |    |
                                                                                                                           |               +-------------------------+ +---------------------+    |
                                                                                                                           |                                                                      |
                                                                                                                           +----------------------------------------------------------------------+
```

### `Security`异常处理

另外，对于`Spring Security`身份认证失败所抛出的`AuthenticationException`异常和权限校验失败所抛出的`AccessDeniedException`异常则是通过异常过滤器`ExceptionTranslationFilter`进行处理的。这样，整个`Spring Security`的执行流程就演变成这样：

```text
                                           SecurityFilterChain
                                           +----------------------------+
                                           |                            |
                                           |    +------------------+    |
                                           |    | Security Filter0 |    |
              +----------+                 |    +--------+---------+    |
              |  Client  |                 |             |              |
              +----+-----+                 |         +--------+         |
 FilterCain        |                       |         +--------+         |
+---------------------------------------+  |             |              |
|        +---------v-----------+        |  |             v              |
|        |       Filter0       |        |  | +-----------+------------+ |
|        +---------+-----------+        |  | |AbstractPreAuthenticated| |
|                  |                    |  | |   ProcessingFilter     | |
|                  |                    |  | +-----------+------------+ |
|   +--------------v----------------+   |  |             |              |      +-----------------------+
|   |     DelegatingFilterProxy     |   |  |         +--------+         |      |                       |
|   | +---------------------------+ |   |  |         +--------+         |      |                       |
|   | |      FilterChainProxy     +------->+             |              |      |                       |
|   | +---------------------------+ |   |  |  +----------v-----------+  |      |             +---------+---------+
|   +-------------------------------+   |  |  |AbstractAuthentication|  |      |             |Continue Processing|
|                  |                    |  |  |  ProcessingFilter    |  |      |             | Request Normally  |
|                  |                    |  |  +----------+-----------+  |      |             +---------+---------+
|        +---------v-----------+        |  |             |              |      |                       |
|        |       FilterN       |        |  |         +--------+         |      |                       |
|        +---------+-----------+        |  |         +--------+         |      |                       +
|                  |                    |  |             |              |      |           Security Exception Judgment
|                  |                    |  |  +----------+-----------+  |      |       +---------------------------------+
|        +---------v-----------+        |  |  | ExceptionTranslation +---------+       |                                 |
|        |       Servlet       |        |  |  |        Filter        |  |              v                                 |
|        +---------------------+        |  |  +----------+-----------+  |  Start Authentication            Access Denied v
+---------------------------------------+  |             |              |  +----------------------------+  +-------------------------+
                                           |  +----------+-----------+  |  | +------------------------+ |  | +---------------------+ |
                                           |  |    FilterSecurity    |  |  | | SecurityContextHolder  | |  | | AccessDeniedHandler | |
                                           |  |     Interceptor      |  |  | +------------------------+ |  | +---------------------+ |
                                           |  +----------+-----------+  |  | +------------------------+ |  +-------------------------+
                                           |             |              |  | |      RequestCache      | |
                                           |    +--------v---------+    |  | +------------------------+ |
                                           |    | Security FilterN |    |  | +------------------------+ |
                                           |    +------------------+    |  | |AuthenticationEntryPoint| |
                                           |                            |  | +------------------------+ |
                                           +----------------------------+  +----------------------------+
```

## `Security`实战：前后端分离

在实际开发中，为了能让身份认证和权限校验操作起来更加灵活，笔者建议在过滤器拦截阶段对所有请求实行放行策略，而在`AOP`拦截阶段通过注解的方式对需要身份认证和权限校验的方法请求进行拦截判断。下面，笔者将基于此设计理念展示代码的实现示例。

### `Security`安全配置

首先需要声明`Spring Security`的配置类，并进行一系列的属性设置。

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 用于获取身份信息
     */
    private final AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService;
    /**
     * 用于处理身份认证失败的情况
     */
    private final AuthenticationEntryPoint authenticationEntryPoint;
    /**
     * 用于处理权限校验失败的情况
     */
    private final AccessDeniedHandler accessDeniedHandler;

    public WebSecurityConfig(
            AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler) {
        this.authenticationUserDetailsService = authenticationUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(this.authenticationUserDetailsService);
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 跨域
                .cors()
                .and()
                // CSRF
                .csrf().disable()
                // header
                .headers()
                .httpStrictTransportSecurity().disable()
                .frameOptions().disable()
                .and()
                // 配置 anonymous
                .anonymous()
                .principal(0)
                .and()
                .addFilter(new JwtPreAuthenticatedProcessingFilter(authenticationManager()))
                // 授权异常
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .accessDeniedHandler(this.accessDeniedHandler)
                // 不创建会话
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 默认所有请求通过，在需要权限的方法加上安全注解
                .and()
                .authorizeRequests()
                .anyRequest().permitAll()
        ;
    }
}
```

### `Security`访问身份获取

其中，对于身份信息的获取/设置是通过`JwtPreAuthenticatedProcessingFilter`实现的，即：

```java
public class JwtPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    public JwtPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(RequestParamsConstants.TOKEN))
                .map(SecurityUtils::parseToken)
                .map(JwtObject::getUserId)
                .orElse(null);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(RequestParamsConstants.TOKEN);
    }
}
```

通过`AbstractPreAuthenticatedProcessingFilter`所提供的能力，在`JwtPreAuthenticatedProcessingFilter`的实现上我们只需要提供关于`principal`和`credentials`的获取方式即可。

### `Security`访问身份校验

然后在用户身份信息获取完成后，我们就可以对它进行身份认证和权限校验了。而由于在`Security`配置时对过滤器的拦截阶段实行放行策略，所以我们需要进一步实现/设置以注解的方式对身份认证和权限校验进行拦截判断。

默认情况下，我们可以通过在`@Configuration`类中声明`@EnableGlobalMethodSecurity`注解并指定其中对应的属性来开启相关注解的使用，但是经过我们前面的改造和设计使用默认提供的注解显然不那么适合，因为它们难以区分身份认证造成的访问失败还是权限校验所造成的访问失败（只会抛出`AccessDeniedException`异常）。因此，为了更加灵活地进行身份认证和权限校验，我们需要自定义一些功能注解。

对于自定义的权限注解，我们只需要进行`3`个处理步骤：

1. 定义权限注解
2. 定义权限`ConfigAttribute`
3. 定义权限`SecurityMetadataSource`

其中，关于从`SecurityMetadataSource`中获取`ConfigAttribute`属性在`SecuredAnnotationSecurityMetadataSource`上其实已经构建了一个很好用的框架了，通过它将大大地降低我们定义权限注解的成本。在使用上，我们只需要声明对应的`AnnotationMetadataExtractor`，定义从注解转换为`ConfigAttribute`的逻辑，最后通过构造参数传入即可。

基于这种设计思路，下面笔者定义了两个权限注解，即：

```java
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Auth {
}

public class AuthorizeConfigAttribute implements ConfigAttribute {
    @Override
    public String getAttribute() {
        return "";
    }
}

public class JwtAuthorizeMetadataSource extends SecuredAnnotationSecurityMetadataSource {
    public JwtAuthorizeMetadataSource() {
        super(new AuthorizeMetadataExtractor());
    }

    public JwtAuthorizeMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class AuthorizeMetadataExtractor implements AnnotationMetadataExtractor<Auth> {

        public Collection<ConfigAttribute> extractAttributes(Auth auth) {
            return Collections.singletonList(new AuthorizeConfigAttribute());
        }
    }
}
```

```java
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Permission {
    /**
     * Returns the list of security configuration attributes (e.g.&nbsp;permission1, permission2).
     *
     * @return String[] The secure method attributes
     */
    String[] value();
}

public class PermissionConfigAttribute extends SecurityConfig {

    public PermissionConfigAttribute(String config) {
        super(config);
    }
}

public class JwtPermissionMetadataSource extends SecuredAnnotationSecurityMetadataSource {

    public JwtPermissionMetadataSource() {
        super(new PermissionMetadataExtractor());
    }

    public JwtPermissionMetadataSource(AnnotationMetadataExtractor annotationMetadataExtractor) {
        super(annotationMetadataExtractor);
    }

    private static class PermissionMetadataExtractor implements AnnotationMetadataExtractor<Permission> {

        public Collection<ConfigAttribute> extractAttributes(Permission permission) {
            return Arrays.stream(permission.value())
                    .map(PermissionConfigAttribute::new)
                    .collect(Collectors.toList());
        }
    }
}
```

在完成权限注解、权限`ConfigAttribute`和权限`SecurityMetadataSource`的定义后，我们将它们注册到`GlobalMethodSecurityConfiguration`中，即：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    /**
     * 重写AccessDecisionManager。让自定义的AuthorizeVoter和PermissionVoter具有更高的优先级。
     */
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        AccessDecisionManager accessDecisionManager = super.accessDecisionManager();
        if (Objects.nonNull(accessDecisionManager)
                && accessDecisionManager instanceof AffirmativeBased) {
            AffirmativeBased affirmativeBased = (AffirmativeBased) accessDecisionManager;
            List<AccessDecisionVoter<?>> voters = new ArrayList<>();
            voters.add(new AuthorizeVoter());
            voters.add(new PermissionVoter());
            voters.addAll(affirmativeBased.getDecisionVoters());
            return new UnanimousBased(voters);
        }

        return accessDecisionManager;
    }

    /**
     * 重写MethodSecurityMetadataSource，让自定义的AuthorizeMetadataSource和PermissionMetadataSource具有更高的优先级
     */
    @Bean
    @Override
    public MethodSecurityMetadataSource methodSecurityMetadataSource() {
        DelegatingMethodSecurityMetadataSource delegating = (DelegatingMethodSecurityMetadataSource) super.methodSecurityMetadataSource();
        List<MethodSecurityMetadataSource> metadataSourceList = new ArrayList<>();
        metadataSourceList.add(new JwtAuthorizeMetadataSource());
        metadataSourceList.add(new JwtPermissionMetadataSource());
        metadataSourceList.addAll(delegating.getMethodSecurityMetadataSources());
        return new JwtDelegatingMetadataSource(metadataSourceList);
    }

}
```

> 需要注意，在继承`GlobalMethodSecurityConfiguration`重写时需要把注解`@EnableGlobalMethodSecurity`放到其类上修饰，否则在加入`@Configuration`注解进行注册时会报`Bean`已重复错误。

最后，在完成定义和注册后我们就可以在对应的方法上声明校验身份的`@Auth`注解或者校验权限的`@Permission`注解，即：

```java
@RestController
@Api(tags = "访问路由")
@RequestMapping(value = "/example")
public class ExampleController {

    @ApiOperation(value = "匿名访问")
    @PostMapping(value = "/v1/anonymousAccess")
    public void anonymousAccess() {
    }

    @Auth
    @ApiOperation(value = "认证访问")
    @PostMapping(value = "/v1/authAccess")
    public void authAccess() {
    }

    @Auth
    @ApiOperation(value = "权限访问")
    @Permission(value = {"permission:access"})
    @PostMapping(value = "/v1/permissionAccess")
    public void permissionAccess() {
    }

}
```

## 更多

通过阅读本文应该对`Spring Security`的整体结构有一个基本的认识了，如果读者想更进一步了解更多详情，可阅读笔者的博客[《`Spring Security`快速入门》](https://blog.omghaowan.com/archives/springsecurity-kuai-su-ru-men)。
