package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import com.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import com.astrsomn.core.common.entity.AiToolEntity;

@Mapper
public interface AiToolMapper extends BaseMapper<AiToolEntity> {

    IPage<AiToolResponseDTO> queryPage(IPage<AiToolResponseDTO> page, @Param("req") AiToolQueryRequestDTO param);
}
