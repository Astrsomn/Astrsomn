package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import com.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import com.astrsomn.core.common.entity.AiAgentEntity;

@Mapper
public interface AiAgentMapper extends BaseMapper<AiAgentEntity> {


    IPage<AiAgentResponseDTO> queryPage(IPage<AiAgentResponseDTO> page,@Param("req") AiAgentQueryRequestDTO param);



}
