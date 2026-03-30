package org.astrsomn.server.service.support;

import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 业务资源 key 统一生成：model / agent / prompt / tool / mcp 在各自表内、按
 * {@code CREATE_USER} 维度唯一；key 段之间使用冒号分隔，便于跨环境复用。
 * <p>
 * <b>Model</b>：{@code model:{provider}:{modelName}:u:{user}}<br>
 * <b>其它</b>：{@code {prefix}:{name}:u:{user}}，prefix 见 {@link BizKeyNamespace}
 */
@Component
public class BizResourceKeyGenerator {

    public static final int MAX_KEY_LEN = 120;
    private static final int MAX_SLUG_LEN = 48;

    /**
     * 用于在持久化前检测是否已存在相同 key。
     */
    @FunctionalInterface
    public interface KeyOccurrenceCounter {
        long count(String candidateKey);
    }

    /**
     * Model：{@code model:{provider}:{modelName}:u:{user}}，冲突时 {@code :1}、{@code :2}…
     */
    public String generateUniqueModelKey(AiModelEntity entity, KeyOccurrenceCounter counter) {
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String provider = StringUtils.defaultIfBlank(entity.getProvider(), "unk");
        String modelName = StringUtils.defaultIfBlank(entity.getModelName(), "model");

        String base = joinSegments(BizKeyNamespace.MODEL.getPrefix(), provider, modelName, "u", user);
        base = truncate(base, MAX_KEY_LEN);
        return allocateWithSuffix(base, counter);
    }

    /**
     * Agent / Prompt / Tool / MCP：{@code agent:{name}:u:{user}} 等。
     */
    public String generateUniqueBizKey(
            BizKeyNamespace namespace,
            String createUser,
            String displayName,
            KeyOccurrenceCounter counter) {
        String user = StringUtils.defaultIfBlank(createUser, "0");
        String name = StringUtils.defaultIfBlank(displayName, "resource");

        String base = joinSegments(namespace.getPrefix(), name, "u", user);
        base = truncate(base, MAX_KEY_LEN);
        return allocateWithSuffix(base, counter);
    }

    private static String allocateWithSuffix(String base, KeyOccurrenceCounter counter) {
        String candidate = base;
        for (int i = 0; i < 1000; i++) {
            if (i > 0) {
                String suffix = ":" + i;
                candidate = truncate(base, MAX_KEY_LEN - suffix.length()) + suffix;
            }
            if (counter.count(candidate) == 0) {
                return candidate;
            }
        }
        return truncate(base + ":" + System.currentTimeMillis(), MAX_KEY_LEN);
    }

    private static String joinSegments(String... parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            String normalized = slug(part);
            if (builder.length() > 0) {
                builder.append(':');
            }
            builder.append(normalized);
        }
        return builder.toString();
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
