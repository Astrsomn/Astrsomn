package com.astrsomn.provider.tencent;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TencentModelApiClientTest {

    // No API key in market config — test disabled until key is provided
    private static final String API_KEY = "";

    @Test
    @Disabled("No API key configured for Tencent")
    void listModels_withDefaultBaseUrl() {
        TencentModelApiClient client = new TencentModelApiClient();
        List<ProviderModelDTO> models = client.listModels(API_KEY, null);

        System.out.println("=== Tencent (default URL) ===");
        System.out.println("Models found: " + models.size());
        for (ProviderModelDTO m : models) {
            System.out.println("  - " + m.getModelKey() + " | " + m.getModelName() + " | type=" + m.getModelType());
        }

        assertFalse(models.isEmpty(), "Should return at least one model");
    }
}
