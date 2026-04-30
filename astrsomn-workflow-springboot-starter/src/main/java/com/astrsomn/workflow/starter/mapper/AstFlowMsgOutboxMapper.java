package com.astrsomn.workflow.starter.mapper;

import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowMsgOutboxEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowMsgOutboxMapper extends BaseMapper<AstFlowMsgOutboxEntity> {

    IPage<AstFlowMsgOutboxResponseDTO> queryPage(
            IPage<AstFlowMsgOutboxResponseDTO> page,
            @Param("req") AstFlowMsgOutboxQueryRequestDTO param);

    AstFlowMsgOutboxResponseDTO detail(@Param("id") Long id, @Param("envCode") String envCode);
}
