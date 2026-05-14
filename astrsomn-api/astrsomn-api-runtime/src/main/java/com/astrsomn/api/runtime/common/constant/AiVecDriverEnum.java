package com.astrsomn.api.runtime.common.constant;

import com.astrsomn.common.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface AiVecDriverEnum {


    @Getter
    @AllArgsConstructor
    enum Provider implements BaseEnum {


        QDRANT("qdrant", "Qdrant Vector Database"),


        MILVUS("milvus", "Zilliz / Milvus"),


        PINECONE("pinecone", "Pinecone Managed Service"),


        CHROMA("chroma", "Chroma AI"),


        DASHVECTOR("dashvector", "Alibaba Cloud DashVector"),


        WEAVIATE("weaviate", "Weaviate Vector Search"),


        ELASTICSEARCH("elasticsearch", "Elasticsearch / OpenSearch"),


        PGVECTOR("pgvector", "PostgreSQL pgvector Extension"),


        REDIS("redis", "Redis Search & Query"),


        IN_MEMORY("in_memory", "Local In-Memory Store");

        private final String code;
        private final String desc;

    }


    @Getter
    @AllArgsConstructor
    enum ParamEnum implements BaseEnum {

        HOST("host", "Host"),
        PORT("port", "Port"),
        USERNAME("username", "Username"),
        PASSWORD("password", "Password"),
        DATABASE_NAME("databaseName", "Database Name"),
        TOKEN("token", "Token"),
        ;


        private final String code;
        private final String desc;
    }


    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {


        ENABLED("enabled", "Enabled"),


        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }
}