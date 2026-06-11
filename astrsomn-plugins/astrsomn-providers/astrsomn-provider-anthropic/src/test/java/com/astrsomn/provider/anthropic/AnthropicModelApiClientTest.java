package com.astrsomn.provider.anthropic;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnthropicModelApiClientTest {

    // API key uses env var ${ANTHROPIC_API_KEY} — test disabled by default
    private static final String API_KEY = System.getenv().getOrDefault("ANTHROPIC_API_KEY", "");

    @Test
    @Disabled("Requires ANTHROPIC_API_KEY env var")
    void listModels_withDefaultBaseUrl() {
        AnthropicModelApiClient client = new AnthropicModelApiClient();
        List<ProviderModelDTO> models = client.listModels(API_KEY, null);

        System.out.println("=== Anthropic (default URL) ===");
        System.out.println("Models found: " + models.size());
        for (ProviderModelDTO m : models) {
            System.out.println("  - " + m.getModelKey() + " | " + m.getModelName() + " | type=" + m.getModelType());
        }

        assertFalse(models.isEmpty(), "Should return at least one model");
    }
}
