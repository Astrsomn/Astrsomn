package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiModelEntity;

import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.server.service.AiModelService;
import org.springframework.stereotype.Service;

@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {


    @Override
    public BaseResponse<String> delete(long[] longIds) {
        return null;
    }

    @Override
    public PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request) {
        return null;
    }

    @Override
    public BaseResponse<AiModelResponseDTO> detail(Long longId) {
        return null;
    }

    @Override
    public BaseResponse<String> updateModel(AiModelUpdateRequestDTO request) {
        return null;
    }

    @Override
    public BaseResponse<String> create(AiModelCreateRequestDTO request) {
        return null;
    }
}
