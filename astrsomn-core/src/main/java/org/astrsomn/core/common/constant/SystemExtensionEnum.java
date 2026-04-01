package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

public interface SystemExtensionEnum {

    @Getter
    @AllArgsConstructor
    enum ExtensionTypeEnum implements BaseEnum {

        MCP("MCP", "MCP"),
        MODEL_PROVIDER("MODEL_PROVIDER", "model Provider"),
        VECTOR_STORE("VECTOR_STORE", "vectorStore");

        private String code;

        private String desc;

    }


}
