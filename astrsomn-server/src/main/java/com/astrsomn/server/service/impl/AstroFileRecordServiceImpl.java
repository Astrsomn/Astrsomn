package com.astrsomn.server.service.impl;

import com.astrsomn.server.service.AstroFileRecordService;
import com.astrsomn.api.storage.entity.AstroFileRecordEntity;
import com.astrsomn.internal.storage.mapper.AstroFileRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AstroFileRecordServiceImpl extends ServiceImpl<AstroFileRecordMapper, AstroFileRecordEntity>
        implements AstroFileRecordService {
}
