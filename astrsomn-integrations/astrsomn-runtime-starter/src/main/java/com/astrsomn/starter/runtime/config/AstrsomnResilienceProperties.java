package com.astrsomn.starter.runtime.config;

import lombok.Data;

/**
 * Astrsomn 与 Resilience4j 相关的预留配置（命名实例仍在宿主 {@code resilience4j.*} 下声明）。
 */
@Data
public class AstrsomnResilienceProperties {

    /**
     * 关闭后仍保留自动配置类加载，便于后续扩展；当前不影响路由组合行为。
     */
    private boolean enabled = true;
}
