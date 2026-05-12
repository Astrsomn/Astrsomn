package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.model.AiModelQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.model.AiModelResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiModelMapper extends BaseMapper<AiModelEntity> {

    IPage<AiModelResponseDTO> queryPage(IPage<AiModelResponseDTO> page,
                                        @Param("req") AiModelQueryRequestDTO param);

    AiModelResponseDTO selectModelWithReferenceStatus(@Param("id") Long id);
}
