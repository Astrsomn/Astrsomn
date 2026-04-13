package org.astrsomn.vector.chroma.service;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.entity.AiVecDriverEntity;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDriver;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;
import org.astrsomn.core.common.utils.EnumUtils;

import java.util.List;
public final class ChromaDriverHandler extends AbstractVecDriver {

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.CHROMA.getCode();
    }

    @Override
    public VecSource bindSource(AiVecSourceEntity source) {
        return new ChromaVecSourceHandler(source);
    }


    @Override
    public AiVecDriverEntity getDriverEntity() {
        List<AiVecDriverEnum.ParamEnum> allowedParams = List.of(
                AiVecDriverEnum.ParamEnum.HOST,
                AiVecDriverEnum.ParamEnum.PORT,
                AiVecDriverEnum.ParamEnum.TOKEN);
        return AiVecDriverEntity.builder()
                .driverName(AiVecDriverEnum.Provider.CHROMA.getDesc())
                .provider(AiVecDriverEnum.Provider.CHROMA.getCode())
                .params(EnumUtils.toCapabilitiesJson(allowedParams))
                .status(AiVecDriverEnum.StatusEnum.ENABLED.getCode())
                .build();
    }
}