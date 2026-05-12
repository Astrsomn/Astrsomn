package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;

@Mapper
public interface AstAiMcpMapper extends BaseMapper<AiMcpEntity> {


}
