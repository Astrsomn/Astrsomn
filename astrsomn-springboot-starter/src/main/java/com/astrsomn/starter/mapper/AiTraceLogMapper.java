package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.core.common.entity.AiTraceLogEntity;

@Mapper
public interface AiTraceLogMapper extends BaseMapper<AiTraceLogEntity> {

    IPage<AiTraceLogResponseDTO> queryPage(IPage<AiTraceLogResponseDTO> page, @Param("req") AiTraceLogQueryRequestDTO param);
}
