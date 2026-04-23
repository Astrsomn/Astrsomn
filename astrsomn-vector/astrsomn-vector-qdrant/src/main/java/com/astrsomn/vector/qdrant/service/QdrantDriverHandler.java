package com.astrsomn.vector.qdrant.service;

import com.astrsomn.core.common.constant.AiVecDriverEnum;
import com.astrsomn.core.common.entity.AiVecDriverEntity;
import com.astrsomn.core.common.entity.AiVecSourceEntity;
import com.astrsomn.core.common.langchain.extension.vector.AbstractVecDriver;
import com.astrsomn.core.common.langchain.extension.vector.VecSource;
import com.astrsomn.core.common.utils.EnumUtils;

import java.util.List;
public final class QdrantDriverHandler extends AbstractVecDriver {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.QDRANT.getCode();
    }

    @Override
    public VecSource bindSource(AiVecSourceEntity source) {
        return new QdrantVecSourceHandler(source);
    }


    @Override
    public AiVecDriverEntity getDriverEntity() {
        List<AiVecDriverEnum.ParamEnum> allowedParams = List.of(
                AiVecDriverEnum.ParamEnum.HOST,
                AiVecDriverEnum.ParamEnum.PORT,
                AiVecDriverEnum.ParamEnum.TOKEN);
        return AiVecDriverEntity.builder()
                .driverName(AiVecDriverEnum.Provider.QDRANT.getDesc())
                .provider(AiVecDriverEnum.Provider.QDRANT.getCode())
                .params(EnumUtils.toCapabilitiesJson(allowedParams))
                .status(AiVecDriverEnum.StatusEnum.ENABLED.getCode())
                .build();
    }
}
