package org.astrsomn.vector.qdrant.service;

import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.entity.AiVecDriverEntity;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.langchain.extension.vector.AbstractVecDriver;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;
import org.astrsomn.core.common.utils.EnumUtils;

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
                AiVecDriverEnum.ParamEnum.USERNAME,
                AiVecDriverEnum.ParamEnum.PASSWORD,
                AiVecDriverEnum.ParamEnum.DATABASE_NAME,
                AiVecDriverEnum.ParamEnum.TOKEN);
        return AiVecDriverEntity.builder()
                .driverName(AiVecDriverEnum.Provider.QDRANT.getDesc())
                .provider(AiVecDriverEnum.Provider.QDRANT.getCode())
                .params(EnumUtils.toCapabilitiesJson(allowedParams))
                .build();
    }
}
