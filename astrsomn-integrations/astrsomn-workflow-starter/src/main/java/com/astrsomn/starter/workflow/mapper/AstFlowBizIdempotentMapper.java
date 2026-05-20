package com.astrsomn.starter.workflow.mapper;

import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.bizidempotent.AstFlowBizIdempotentResponseDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowBizIdempotentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstFlowBizIdempotentMapper extends BaseMapper<AstFlowBizIdempotentEntity> {

    IPage<AstFlowBizIdempotentResponseDTO> queryPage(
            IPage<AstFlowBizIdempotentResponseDTO> page,
            @Param("req") AstFlowBizIdempotentQueryRequestDTO param);

    AstFlowBizIdempotentResponseDTO detail(@Param("id") Long id);
}
