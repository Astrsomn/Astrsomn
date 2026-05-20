package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowInstanceMapper extends BaseMapper<AstFlowInstanceEntity> {

    IPage<AstFlowInstanceResponseDTO> queryPage(
            IPage<AstFlowInstanceResponseDTO> page,
            @Param("req") AstFlowInstanceQueryRequestDTO param);

    AstFlowInstanceResponseDTO detail(@Param("id") Long id);
}
