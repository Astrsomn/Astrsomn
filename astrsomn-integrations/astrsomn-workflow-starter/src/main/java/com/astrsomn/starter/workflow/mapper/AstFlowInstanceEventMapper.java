package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEventEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowInstanceEventMapper extends BaseMapper<AstFlowInstanceEventEntity> {

    IPage<AstFlowInstanceEventResponseDTO> queryPage(
            IPage<AstFlowInstanceEventResponseDTO> page,
            @Param("req") AstFlowInstanceEventQueryRequestDTO param);

    AstFlowInstanceEventResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
