package com.astrsomn.workflow.starter.mapper;

import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.timerjob.AstFlowTimerJobResponseDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowTimerJobEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowTimerJobMapper extends BaseMapper<AstFlowTimerJobEntity> {

    IPage<AstFlowTimerJobResponseDTO> queryPage(
            IPage<AstFlowTimerJobResponseDTO> page,
            @Param("req") AstFlowTimerJobQueryRequestDTO param);

    AstFlowTimerJobResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
