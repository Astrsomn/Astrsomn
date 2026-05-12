package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;

@Mapper
public interface AstAiInstanceMapper extends BaseMapper<AiInstanceEntity> {

}
