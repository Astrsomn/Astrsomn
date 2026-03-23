package org.astrsomn.server.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.server.service.AiAgentService;
import org.springframework.stereotype.Service;

@Service
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {
}
