package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.server.service.AiMcpService;
import org.springframework.stereotype.Service;

@Service
public class AiMcpServiceImpl extends ServiceImpl<AiMcpMapper, AiMcpEntity> implements AiMcpService {
}
