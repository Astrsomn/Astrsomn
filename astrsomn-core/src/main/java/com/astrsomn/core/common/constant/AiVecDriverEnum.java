package com.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.astrsomn.core.common.base.BaseEnum;

public interface AiVecDriverEnum {


    @Getter
    @AllArgsConstructor
    enum Provider implements BaseEnum {

        /**
         * 高性能向量数据库，支持 Payload 过滤，适合 Bento Grid 风格的高速查询
         */
        QDRANT("qdrant", "Qdrant Vector Database"),

        /**
         * 企业级标准，支持海量数据与高度结构化的元数据
         */
        MILVUS("milvus", "Zilliz / Milvus"),

        /**
         * 云原生标准，国外最流行的托管式向量库
         */
        PINECONE("pinecone", "Pinecone Managed Service"),

        /**
         * 轻量级开源首选，AI 社区活跃度极高
         */
        CHROMA("chroma", "Chroma AI"),

        /**
         * 阿里云出品，国内企业级 RAG 应用常用
         */
        DASHVECTOR("dashvector", "Alibaba Cloud DashVector"),

        /**
         * 具备丰富语义搜索特性的向量库
         */
        WEAVIATE("weaviate", "Weaviate Vector Search"),

        /**
         * 适合已集成搜索系统的企业，利用现有 ES 集群进行向量检索
         */
        ELASTICSEARCH("elasticsearch", "Elasticsearch / OpenSearch"),

        /**
         * 基于 PostgreSQL 的扩展，适合对关系型数据有强一致性要求的场景
         */
        PGVECTOR("pgvector", "PostgreSQL pgvector Extension"),

        /**
         * 极速缓存场景下的向量检索方案
         */
        REDIS("redis", "Redis Search & Query"),

        /**
         * 本地化开发测试使用（LangChain4j 内存实现）
         */
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
        TOKEN("token", "Token"),;



        private final String code;
        private final String desc;
    }


    @Getter
    @AllArgsConstructor
    enum StatusEnum implements BaseEnum {

        /**
         * Enable
         */
        ENABLED("enabled", "Enabled"),

        /**
         * Disable
         */
        DISABLED("disabled", "Disable");

        private String code;

        private String desc;

    }
}
