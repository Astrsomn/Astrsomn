package com.astrsomn.provider.zhipu;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZhipuModelApiClientTest {

    // No API key in market config — test disabled until key is provided
    private static final String API_KEY = "";

    @Test
    @Disabled("No API key configured for Zhipu")
    void listModels_withDefaultBaseUrl() {
        ZhipuModelApiClient client = new ZhipuModelApiClient();
        List<ProviderModelDTO> models = client.listModels(API_KEY, null);

        System.out.println("=== Zhipu (default URL) ===");
        System.out.println("Models found: " + models.size());
        for (ProviderModelDTO m : models) {
            System.out.println("  - " + m.getModelKey() + " | " + m.getModelName() + " | type=" + m.getModelType());
        }

        assertFalse(models.isEmpty(), "Should return at least one model");
    }
}
