package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiSensitiveWordMapper extends BaseMapper<AiSensitiveWordEntity> {

    IPage<AiSensitiveWordResponseDTO> queryPage(IPage<AiSensitiveWordResponseDTO> page,
                                                @Param("req") AiSensitiveWordQueryRequestDTO param);
}
