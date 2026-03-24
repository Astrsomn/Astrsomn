package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import org.astrsomn.core.common.entity.AiTemplateEntity;

@Mapper
public interface AiTemplateMapper extends BaseMapper<AiTemplateEntity> {

    IPage<AiTemplateResponseDTO> queryPage(IPage<AiTemplateResponseDTO> page, @Param("req") AiTemplateQueryRequestDTO param);
}
