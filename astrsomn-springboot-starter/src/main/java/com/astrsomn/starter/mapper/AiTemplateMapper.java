package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import com.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import com.astrsomn.core.common.entity.AiTemplateEntity;

@Mapper
public interface AiTemplateMapper extends BaseMapper<AiTemplateEntity> {

    IPage<AiTemplateResponseDTO> queryPage(IPage<AiTemplateResponseDTO> page, @Param("req") AiTemplateQueryRequestDTO param);
}
