package com.astrsomn.workflow.starter.mapper;

import com.astrsomn.workflow.core.domain.entity.AstFlowDefinitionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.definition.AstFlowDefinitionResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowDefinitionMapper extends BaseMapper<AstFlowDefinitionEntity> {

    IPage<AstFlowDefinitionResponseDTO> queryPage(
            IPage<AstFlowDefinitionResponseDTO> page,
            @Param("req") AstFlowDefinitionQueryRequestDTO param);

    AstFlowDefinitionResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
