package org.astrsomn.server.config;

import org.astrsomn.server.interceptor.AuthenticationInterceptor;
import org.astrsomn.server.interceptor.AuthorizationInterceptor;
import org.astrsomn.server.interceptor.LoggingInterceptor;
import org.astrsomn.server.interceptor.RateLimitingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        // TODO: 配置拦截器链和拦截路径
        
        // 1. 日志拦截器 - 拦截所有请求
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1);
        
        // 2. 认证拦截器 - 排除登录接口和静态资源
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/refresh-token",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                )
                .order(2);
        
        // 3. 权限拦截器 - 拦截需要权限的接口
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/agent/**", "/api/model/**", "/api/tool/**")
                .excludePathPatterns("/api/agent/list", "/api/model/list")
                .order(3);
        
        // 4. 限流拦截器 - 拦截所有API请求
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/**")
                .order(4);
    }
}
