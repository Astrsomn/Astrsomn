package com.astrsomn.api.runtime.common.model;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO.ParamDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Default model parameter and capability definitions.
 * <p>
 * Most providers follow the OpenAI-compatible API protocol, so they can use
 * {@link #chatDefaults()} / {@link #embeddingDefaults()} / {@link #imageDefaults()}
 * directly. Providers with special needs (e.g. Anthropic's extended thinking,
 * Gemini's safety settings) should create their own definitions.
 * </p>
 */
public final class ModelParamDefaults {

    private ModelParamDefaults() {}

    // ── Capability definitions ──

    /** Default capabilities for a chat model (OpenAI-compatible). */
    public static List<String> chatCaps() {
        return new ArrayList<>(List.of("STREAMING", "TOOLS", "JSON_MODE"));
    }

    /** Default capabilities for an embedding model. */
    public static List<String> embeddingCaps() {
        return List.of("TEXT_EMBEDDING");
    }

    /** Default capabilities for an image generation model. */
    public static List<String> imageCaps() {
        return List.of("TEXT_TO_IMAGE");
    }

    /** Look up default capabilities by model type. */
    public static List<String> capsByType(String modelType) {
        return switch (modelType != null ? modelType : "chat") {
            case "embedding" -> embeddingCaps();
            case "image"     -> imageCaps();
            default          -> chatCaps();
        };
    }

    // ── Parameter definitions ──

    /** Default parameters for a chat model (OpenAI-compatible). */
    public static List<ParamDef> chatParams() {
        return List.of(
            param("temperature",      "Temperature",      "temperature",      "0.7",  "0-2.0",   false),
            param("top_p",            "Top P",            "top_p",            "1.0",  "0-1.0",   false),
            param("top_k",            "Top K",            "top_k",            "",     "int",     false),
            param("max_tokens",       "Max Tokens",       "max_tokens",       "2048", "1-32k",   false),
            param("presence_penalty", "Presence Penalty", "presence_penalty", "0",    "-2-2",    false),
            param("frequency_penalty","Frequency Penalty","frequency_penalty","0",    "-2-2",    false),
            param("stop_sequences",   "Stop Sequences",   "stop_sequences",   "",     "Array",   false),
            param("seed",             "Seed",             "seed",             "",     "int",     false),
            param("logit_bias",       "Logit Bias",       "logit_bias",       "",     "Object",  false)
        );
    }

    /** Default parameters for an embedding model. */
    public static List<ParamDef> embeddingParams() {
        return List.of(
            param("dimensions",  "Dimensions",  "dimensions",  "1536", "int",    false),
            param("model_name",  "Model Name",  "model_name",  "",     "string", false),
            param("user",        "User",        "user",        "",     "string", false)
        );
    }

    /** Default parameters for an image generation model. */
    public static List<ParamDef> imageParams() {
        return List.of(
            param("size",            "Size",            "size",            "1024x1024", "string",       false),
            param("quality",         "Quality",         "quality",         "standard",  "standard/hd",  false),
            param("style",           "Style",           "style",           "vivid",     "vivid/natural",false),
            param("response_format", "Response Format", "response_format", "url",       "url/b64_json", false)
        );
    }

    /** Look up default parameters by model type. */
    public static List<ParamDef> paramsByType(String modelType) {
        return switch (modelType != null ? modelType : "chat") {
            case "embedding" -> embeddingParams();
            case "image"     -> imageParams();
            default          -> chatParams();
        };
    }

    // ── Model-ID-based capability inference ──

    /**
     * Infer capabilities from a model ID, for OpenAI-compatible providers.
     * Falls back to {@link #capsByType(String)} when no pattern matches.
     */
    public static List<String> inferCapsFromId(String modelId, String modelType) {
        if (!"chat".equals(modelType)) {
            return capsByType(modelType);
        }
        String id = modelId.toLowerCase();

        List<String> caps = new ArrayList<>(List.of("STREAMING", "TOOLS"));

        // Vision — gpt-4o series, gpt-4-turbo, gpt-4-vision, claude-3+, gemini
        if (id.contains("gpt-4o") || id.contains("gpt-4-turbo") || id.contains("gpt-4-vision")
                || id.contains("claude-3") || id.contains("gemini")
                || id.contains("qwen-vl") || id.contains("glm-4v")) {
            caps.add("VISION");
        }

        // Deep reasoning — o1, o3, deepseek-reasoner, kimi-k2
        if (id.startsWith("o1") || id.startsWith("o3")
                || id.contains("deepseek-reasoner") || id.contains("k2")) {
            caps.add("DEEP_REASONING");
        }

        // JSON mode — most models except gpt-3.5 and some older ones
        if (!id.contains("gpt-3.5")) {
            caps.add("JSON_MODE");
        }

        return caps;
    }

    /**
     * Infer parameters from a model ID, for OpenAI-compatible providers.
     * Handles reasoning models (o1/o3) specially — temperature is less relevant,
     * max_tokens default is higher.
     */
    public static List<ParamDef> inferParamsFromId(String modelId, String modelType) {
        if (!"chat".equals(modelType)) {
            return paramsByType(modelType);
        }

        String id = modelId.toLowerCase();
        boolean isReasoning = id.startsWith("o1") || id.startsWith("o3")
                || id.contains("deepseek-reasoner");

        if (isReasoning) {
            // Reasoning models: max_tokens higher, temperature less useful
            List<ParamDef> params = new ArrayList<>(chatParams());
            // Override max_tokens default
            params.replaceAll(p -> {
                if ("max_tokens".equals(p.getId())) {
                    return param("max_tokens", "Max Tokens", "max_tokens", "4096", "1-128k", false);
                }
                if ("temperature".equals(p.getId())) {
                    return param("temperature", "Temperature", "temperature", "1", "0-2", false);
                }
                return p;
            });
            return params;
        }

        return chatParams();
    }

    // ── Helper ──

    /** Shortcut to build a ParamDef. */
    public static ParamDef param(String id, String desc, String mapping,
                                  String defaultValue, String range, boolean active) {
        return ParamDef.builder()
                .id(id)
                .desc(desc)
                .mapping(mapping)
                .defaultValue(defaultValue)
                .range(range)
                .active(active)
                .build();
    }
}
