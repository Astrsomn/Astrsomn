package org.astrsomn.server.config;

import org.astrsomn.server.interceptor.AuthenticationInterceptor;
import org.astrsomn.server.interceptor.AuthorizationInterceptor;
import org.astrsomn.server.interceptor.EnvCodeRequestInterceptor;
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

    @Autowired
    private EnvCodeRequestInterceptor envCodeRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        // 1. 日志拦截器 - 拦截所有请求
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1);
        
        // 2. 认证拦截器 - 排除登录接口和静态资源
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**", "/v1/astro/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/refresh-token",
                        "/v1/astro/auth/login",
                        "/v1/astro/auth/refresh-token",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
                )
                .order(2);

        // 2b. 工作空间环境（请求头 X-Astrsomn-Env-Code）→ EnvScope，须在认证之后
        registry.addInterceptor(envCodeRequestInterceptor)
                .addPathPatterns("/api/**", "/v1/astro/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/refresh-token",
                        "/v1/astro/auth/login",
                        "/v1/astro/auth/refresh-token",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/webjars/**"
                )
                .order(3);
        
        // 3. 权限拦截器 - 拦截需要权限的接口
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns(
                        "/v1/astro/ai-agent/**",
                        "/v1/astro/ai-model/**",
                        "/v1/astro/ai-tool/**",
                        "/v1/astro/ai-mcp/**",
                        "/v1/astro/ai-conversation/**",
                        "/v1/astro/ai-promopt/**",
                        "/v1/astro/ai-template/**",
                        "/v1/astro/system-user/**",
                        "/v1/astro/system-env/**"
                )
                .order(4);
        
        // 4. 限流拦截器 - 拦截所有API请求
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/**", "/v1/astro/**")
                .order(5);
    }
}
