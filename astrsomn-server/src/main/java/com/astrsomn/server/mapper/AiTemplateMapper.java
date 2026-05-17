package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiTemplateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiTemplateMapper extends BaseMapper<AiTemplateEntity> {

    IPage<AiTemplateResponseDTO> queryPage(IPage<AiTemplateResponseDTO> page, @Param("req") AiTemplateQueryRequestDTO param);
}
