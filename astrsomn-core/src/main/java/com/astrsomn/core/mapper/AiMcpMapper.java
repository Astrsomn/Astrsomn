package com.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.mcp.AiMcpQueryRequestDTO;
import com.astrsomn.core.common.dto.mcp.AiMcpResponseDTO;
import com.astrsomn.core.common.entity.AiMcpEntity;

@Mapper
public interface AiMcpMapper extends BaseMapper<AiMcpEntity> {

    IPage<AiMcpResponseDTO> queryPage(IPage<AiMcpResponseDTO> page, @Param("req") AiMcpQueryRequestDTO param);
}
