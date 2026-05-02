package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.entity.AstFlowNodeConfigEntity;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowNodeConfigMapper extends BaseMapper<AstFlowNodeConfigEntity> {

    IPage<AstFlowNodeConfigResponseDTO> queryPage(
            IPage<AstFlowNodeConfigResponseDTO> page,
            @Param("req") AstFlowNodeConfigQueryRequestDTO param);

    AstFlowNodeConfigResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
