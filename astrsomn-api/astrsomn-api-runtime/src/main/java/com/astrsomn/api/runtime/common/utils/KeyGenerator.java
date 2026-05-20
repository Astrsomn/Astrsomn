package com.astrsomn.api.runtime.common.utils;

import java.util.UUID;

public class KeyGenerator {


    private static final String AGENT_KEY_PREFIX = "AG-";
    public static final String PROMPT_KEY_PREFIX = "PT-";
    public static final String INSTANCE_KEY_PREFIX = "IN";

    private static final int RANDOM_KEY_LENGTH = 16;


    public static String generateUniqueAgentKey() {
        return AGENT_KEY_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }

    public static String generateUniquePromptKey() {
        return PROMPT_KEY_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }

    public static String generateUniqueInstanceKey() {
        return INSTANCE_KEY_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, RANDOM_KEY_LENGTH);
    }
}
