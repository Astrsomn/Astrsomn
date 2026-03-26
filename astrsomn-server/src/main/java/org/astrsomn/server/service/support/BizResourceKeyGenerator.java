package org.astrsomn.server.service.support;

import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 业务资源 key 统一生成：model / agent / prompt / tool / mcp 在各自表内、按
 * {@code (ENV_CODE, CREATE_USER)} 维度唯一；key 仅含小写、数字、下划线，便于日志与调用方识别。
 * <p>
 * <b>Model</b>（与历史一致，便于已有数据）：{@code {provider}_{modelName}_u{user}}<br>
 * <b>其它</b>：{@code {prefix}_{name}_u{user}}，prefix 见 {@link BizKeyNamespace}
 */
@Component
public class BizResourceKeyGenerator {

    public static final int MAX_KEY_LEN = 120;
    private static final int MAX_SLUG_LEN = 48;

    private final AstrsomnProperties astrsomnProperties;

    public BizResourceKeyGenerator(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    /**
     * 用于在持久化前检测是否已存在相同 key。
     */
    @FunctionalInterface
    public interface KeyOccurrenceCounter {
        long count(String candidateKey);
    }

    /**
     * Model：{@code provider}_{modelName}_u{user}，冲突时 {@code _2}、{@code _3}…
     */
    public String generateUniqueModelKey(AiModelEntity entity, KeyOccurrenceCounter counter) {
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String provider = StringUtils.defaultIfBlank(entity.getProvider(), "unk");
        String modelName = StringUtils.defaultIfBlank(entity.getModelName(), "model");

        String base = slug(provider) + "_" + slug(modelName) + "_u" + slug(user);
        base = truncate(base, MAX_KEY_LEN);
        return allocateWithSuffix(base, counter);
    }

    /**
     * Agent / Prompt / Tool / MCP：{@code agt|prm|tol|mcp}_{name}_u{user}。
     * 环境隔离请在 {@link KeyOccurrenceCounter} 内按 {@code ENV_CODE}（及创建人）查询。
     */
    public String generateUniqueBizKey(
            BizKeyNamespace namespace,
            String createUser,
            String displayName,
            KeyOccurrenceCounter counter) {
        String user = StringUtils.defaultIfBlank(createUser, "0");
        String name = StringUtils.defaultIfBlank(displayName, "resource");

        String base = namespace.getPrefix() + "_" + slug(name) + "_u" + slug(user);
        base = truncate(base, MAX_KEY_LEN);
        return allocateWithSuffix(base, counter);
    }

    private static String allocateWithSuffix(String base, KeyOccurrenceCounter counter) {
        String candidate = base;
        for (int i = 0; i < 1000; i++) {
            if (i > 0) {
                String suffix = "_" + i;
                candidate = truncate(base, MAX_KEY_LEN - suffix.length()) + suffix;
            }
            if (counter.count(candidate) == 0) {
                return candidate;
            }
        }
        return truncate(base + "_" + System.currentTimeMillis(), MAX_KEY_LEN);
    }

    public static String slug(String raw) {
        if (raw == null) {
            return "x";
        }
        String t = raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        t = t.replaceAll("_+", "_").replaceAll("^_+|_+$", "");
        if (t.isEmpty()) {
            return "x";
        }
        return t.length() > MAX_SLUG_LEN ? t.substring(0, MAX_SLUG_LEN) : t;
    }

    public static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max);
    }
}
