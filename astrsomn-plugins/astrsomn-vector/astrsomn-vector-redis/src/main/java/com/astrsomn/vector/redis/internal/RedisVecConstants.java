package com.astrsomn.vector.redis.internal;

/**
 * Shared constants for the Redis vector plugin.
 */
public final class RedisVecConstants {

    /** Metadata key used when storing docId filter on each embedding. */
    public static final String META_DOC_ID_IN_STORE = "doc_id_in_store";

    /** Config JSON key: enable SSL/TLS for Redis connection. */
    public static final String CONFIG_USE_SSL = "useSsl";

    /** Config JSON key: vector index algorithm — "HNSW" (default) or "FLAT". */
    public static final String CONFIG_INDEX_TYPE = "indexType";

    /** Default payload text field name in the Redis index schema. */
    public static final String DEFAULT_PAYLOAD_TEXT_KEY = "text_segment";

    /** Default prefix for Redis hash keys storing embeddings. */
    public static final String DEFAULT_INDEX_PREFIX = "embedding:";

    private RedisVecConstants() {
    }
}
