package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiMcpMapper extends BaseMapper<AiMcpEntity> {

    IPage<AiMcpResponseDTO> queryPage(IPage<AiMcpResponseDTO> page, @Param("req") AiMcpQueryRequestDTO param);
}
