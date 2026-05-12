package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiTraceLogEntity;

@Mapper
public interface AstAiTraceLogMapper extends BaseMapper<AiTraceLogEntity> {


}
