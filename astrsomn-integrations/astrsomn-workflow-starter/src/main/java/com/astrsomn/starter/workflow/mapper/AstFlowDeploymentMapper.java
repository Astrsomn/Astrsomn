package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDeploymentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowDeploymentMapper extends BaseMapper<AstFlowDeploymentEntity> {

    IPage<AstFlowDeploymentResponseDTO> queryPage(
            IPage<AstFlowDeploymentResponseDTO> page,
            @Param("req") AstFlowDeploymentQueryRequestDTO param);

    AstFlowDeploymentResponseDTO detail(@Param("id") Long id);
}
