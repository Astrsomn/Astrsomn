package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import org.astrsomn.core.common.dto.tracelog.AiTraceLogResponseDTO;
import org.astrsomn.core.common.entity.AiTraceLogEntity;

@Mapper
public interface AiTraceLogMapper extends BaseMapper<AiTraceLogEntity> {

    IPage<AiTraceLogResponseDTO> queryPage(IPage<AiTraceLogResponseDTO> page, @Param("req") AiTraceLogQueryRequestDTO param);
}
