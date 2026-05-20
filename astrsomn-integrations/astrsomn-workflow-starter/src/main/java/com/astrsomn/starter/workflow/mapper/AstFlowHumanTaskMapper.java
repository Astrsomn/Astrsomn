package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowHumanTaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowHumanTaskMapper extends BaseMapper<AstFlowHumanTaskEntity> {

    IPage<AstFlowHumanTaskResponseDTO> queryPage(
            IPage<AstFlowHumanTaskResponseDTO> page,
            @Param("req") AstFlowHumanTaskQueryRequestDTO param);

    AstFlowHumanTaskResponseDTO detail(@Param("id") Long id);
}
