package com.astrsomn.server.config;

import com.astrsomn.server.interceptor.*;
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


        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1);


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


        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns(
                        "/v1/astro/ai-agent/**",
                        "/v1/astro/ai-model/**",
                        "/v1/astro/ai-tool/**",
                        "/v1/astro/ai-mcp/**",
                        "/v1/astro/ai-chat-message/**",
                        "/v1/astro/ai-chat-session/**",
                        "/v1/astro/ai-promopt/**",
                        "/v1/astro/ai-template/**",
                        "/v1/astro/ai-workflow/**",
                        "/v1/astro/ai-instance/**",
                        "/v1/astro/ai-account/**",
                        "/v1/astro/system-user/**",
                        "/v1/astro/system-env/**",
                        "/v1/astro/system-message/**",
                        "/v1/astro/sse/**"
                )
                .order(4);


        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/**", "/v1/astro/**")
                .order(5);
    }

}
