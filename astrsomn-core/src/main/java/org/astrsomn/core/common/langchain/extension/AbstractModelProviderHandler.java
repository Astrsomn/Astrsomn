package org.astrsomn.core.common.langchain.extension;

import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractModelProviderHandler implements ModelProviderHandler {

    /**
     * 安全应用参数：当值不为空且模型具备该能力时，执行赋值操作
     */
    protected <T> void apply(List<String> capabilities,
                             AiModelEnum.InferenceParamEnum paramEnum,
                             T value,
                             Consumer<T> setter) {
        if (value != null && paramEnum.containedIn(capabilities)) {
            setter.accept(value);
        }
    }

    /**
     * 辅助方法：解析能力的 JSON 字符串
     */
    protected List<String> parseCapabilities(AiModelEntity entity) {
        List<String> caps = JsonUtil.parseArray(entity.getCapabilities(), String.class);
        return caps == null ? Collections.emptyList() : new ArrayList<>(caps);
    }


}
