package com.astrsomn.workflow.starter.mapper;

import com.astrsomn.workflow.core.domain.entity.AstFlowDeploymentEntity;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowDeploymentMapper extends BaseMapper<AstFlowDeploymentEntity> {

    IPage<AstFlowDeploymentResponseDTO> queryPage(
            IPage<AstFlowDeploymentResponseDTO> page,
            @Param("req") AstFlowDeploymentQueryRequestDTO param);

    AstFlowDeploymentResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
