package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiToolMapper extends BaseMapper<AiToolEntity> {

    IPage<AiToolResponseDTO> queryPage(IPage<AiToolResponseDTO> page, @Param("req") AiToolQueryRequestDTO param);
}
