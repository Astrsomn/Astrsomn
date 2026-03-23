package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.AiPromptEntity;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.server.service.AiPromptService;
import org.springframework.stereotype.Service;

@Service
public class AiPromptServiceImpl extends ServiceImpl<AiPromptMapper, AiPromptEntity> implements AiPromptService {
}
