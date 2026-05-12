package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiAgentMapper extends BaseMapper<AiAgentEntity> {


    IPage<AiAgentResponseDTO> queryPage(IPage<AiAgentResponseDTO> page,@Param("req") AiAgentQueryRequestDTO param);



}
