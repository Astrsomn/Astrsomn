package com.astrsomn.api.runtime.common.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified model data from provider API responses.
 * Each provider ModelApiClient maps its provider-specific response to this DTO.
 */
@Data
public class ProviderModelDTO {

    /** Model key/ID from the provider (e.g. "gpt-4o", "claude-3-5-sonnet-20241022") */
    private String modelKey;

    /** Display name from the provider if available, otherwise same as modelKey */
    private String modelName;

    /** Description from the provider if available */
    private String description;

    /** Inferred model type: chat, embedding, image */
    private String modelType;

    /** Release/created timestamp from the provider */
    private LocalDateTime releasedAt;

    /** Whether the model is deprecated according to the provider */
    private boolean deprecated;

    /** Raw owned_by or organization field from provider response */
    private String ownedBy;

    /**
     * Capability codes, e.g. ["STREAMING", "TOOLS", "VISION", "JSON_MODE", "DEEP_REASONING"]
     * Populated by each provider's own parameter resolver.
     */
    private List<String> capabilities;

    /**
     * Parameter definitions for this model.
     * Populated by each provider's own parameter resolver.
     */
    private List<ParamDef> params;

    /**
     * A single parameter definition.
     * Mirrors the frontend PARAM_TEMPLATES structure for consistency.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParamDef {
        /** Parameter identifier, e.g. "temperature" */
        private String id;
        /** Human-readable description, e.g. "Temperature" */
        private String desc;
        /** API field name this maps to, e.g. "temperature" */
        private String mapping;
        /** Default value, e.g. "0.7" */
        private String defaultValue;
        /** Value range hint, e.g. "0-2.0" or "int" */
        private String range;
        /** Whether this parameter is enabled by default */
        private boolean active;
    }
}
