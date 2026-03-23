package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.astrsomn.server.service.AiConversationService;
import org.springframework.stereotype.Service;

@Service
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversationEntity> implements AiConversationService {
}
