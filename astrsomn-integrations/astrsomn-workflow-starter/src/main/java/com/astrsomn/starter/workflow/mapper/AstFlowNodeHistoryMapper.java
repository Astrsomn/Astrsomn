package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowNodeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowNodeHistoryMapper extends BaseMapper<AstFlowNodeHistoryEntity> {

    IPage<AstFlowNodeHistoryResponseDTO> queryPage(
            IPage<AstFlowNodeHistoryResponseDTO> page,
            @Param("req") AstFlowNodeHistoryQueryRequestDTO param);

    AstFlowNodeHistoryResponseDTO detail(@Param("id") Long id);
}
