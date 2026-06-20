package com.astrsomn.provider.deepseek;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("No API key configured")
class DeepSeekModelApiClientTest {

    private static final String API_KEY = "";

    @Test
    void listModels_withDefaultBaseUrl() {
        DeepSeekModelApiClient client = new DeepSeekModelApiClient();
        List<ProviderModelDTO> models = client.listModels(API_KEY, null);

        System.out.println("=== DeepSeek (default URL) ===");
        System.out.println("Models found: " + models.size());
        for (ProviderModelDTO m : models) {
            System.out.println("  - " + m.getModelKey() + " | " + m.getModelName() + " | type=" + m.getModelType());
        }

        assertFalse(models.isEmpty(), "Should return at least one model");
    }
}
