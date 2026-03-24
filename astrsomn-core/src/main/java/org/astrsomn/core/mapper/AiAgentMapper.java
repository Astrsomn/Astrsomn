package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import org.astrsomn.core.common.entity.AiAgentEntity;

@Mapper
public interface AiAgentMapper extends BaseMapper<AiAgentEntity> {


    IPage<AiAgentResponseDTO> queryPage(IPage<AiAgentResponseDTO> page,@Param("req") AiAgentQueryRequestDTO param);



}
