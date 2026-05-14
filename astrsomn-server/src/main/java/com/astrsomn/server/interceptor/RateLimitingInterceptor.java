package com.astrsomn.server.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, RateCounter> counters = new ConcurrentHashMap<>();
    @Value("${rate.limit.maxRequests:120}")
    private int maxRequests;
    @Value("${rate.limit.windowMillis:60000}")
    private long windowMillis;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = getClientId(request);
        String uri = request.getRequestURI();

        String key = clientId + "|" + uri;
        long now = System.currentTimeMillis();
        RateCounter counter = counters.computeIfAbsent(key, k -> new RateCounter(now));

        // 同一个 key 的窗口重置/计数需要原子性
        synchronized (counter) {
            if (now - counter.windowStartMillis >= windowMillis) {
                counter.windowStartMillis = now;
                counter.count.set(0);
            }
            int current = counter.count.incrementAndGet();
            if (current > maxRequests) {
                response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁\"}");
                return false;
            }
        }

        return true;
    }

    private String getClientId(HttpServletRequest request) {
        return "ip:" + request.getRemoteAddr();
    }

    private static class RateCounter {
        private final AtomicInteger count;
        private volatile long windowStartMillis;

        private RateCounter(long nowMillis) {
            this.count = new AtomicInteger(0);
            this.windowStartMillis = nowMillis;
        }
    }
}
