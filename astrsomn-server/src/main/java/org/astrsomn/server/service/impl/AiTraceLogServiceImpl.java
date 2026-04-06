package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.astrsomn.core.common.entity.AiTraceLogEntity;
import org.astrsomn.core.mapper.AiTraceLogMapper;
import org.astrsomn.server.service.AiTraceLogService;

@Mapper
public class AiTraceLogServiceImpl extends ServiceImpl<AiTraceLogMapper, AiTraceLogEntity> implements AiTraceLogService {
}
