package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiSensitiveWordEntity;

@Mapper
public interface AstAiSensitiveWordMapper extends BaseMapper<AiSensitiveWordEntity> {


}
