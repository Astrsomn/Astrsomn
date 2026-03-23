package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.server.service.AiToolService;
import org.springframework.stereotype.Service;

@Service
public class AiToolServiceImpl extends ServiceImpl<AiToolMapper, AiToolEntity> implements AiToolService {
}
