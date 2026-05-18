package com.astrsomn.vector.milvus.service;

import com.astrsomn.api.runtime.common.langchain.extension.vector.AbstractVecDriver;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.entity.AiVecDriverEntity;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.common.utils.EnumUtils;

import java.util.List;

public final class MilvusDriverHandler extends AbstractVecDriver {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.MILVUS.getCode();
    }

    @Override
    public VecSource bindSource(AiVecSourceEntity source) {
        return new MilvusVecSourceHandler(source);
    }

    @Override
    public AiVecDriverEntity getDriverEntity() {
        List<AiVecDriverEnum.ParamEnum> allowedParams = List.of(
                AiVecDriverEnum.ParamEnum.HOST,
                AiVecDriverEnum.ParamEnum.PORT,
                AiVecDriverEnum.ParamEnum.USERNAME,
                AiVecDriverEnum.ParamEnum.PASSWORD,
                AiVecDriverEnum.ParamEnum.DATABASE_NAME);
        return AiVecDriverEntity.builder()
                .driverName(AiVecDriverEnum.Provider.MILVUS.getDesc())
                .provider(AiVecDriverEnum.Provider.MILVUS.getCode())
                .params(EnumUtils.toCapabilitiesJson(allowedParams))
                .status(AiVecDriverEnum.StatusEnum.ENABLED.getCode())
                .build();
    }
}
