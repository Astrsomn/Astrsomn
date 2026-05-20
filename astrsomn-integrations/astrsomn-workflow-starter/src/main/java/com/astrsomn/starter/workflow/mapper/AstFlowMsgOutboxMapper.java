package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowMsgOutboxEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowMsgOutboxMapper extends BaseMapper<AstFlowMsgOutboxEntity> {

    IPage<AstFlowMsgOutboxResponseDTO> queryPage(
            IPage<AstFlowMsgOutboxResponseDTO> page,
            @Param("req") AstFlowMsgOutboxQueryRequestDTO param);

    AstFlowMsgOutboxResponseDTO detail(@Param("id") Long id);
}
