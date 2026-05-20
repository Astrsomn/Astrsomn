package com.astrsomn.server.service.file;

import com.astrsomn.api.storage.entity.AstFileRecordEntity;
import com.astrsomn.internal.storage.mapper.AstroFileRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AstroFileRecordServiceImpl extends ServiceImpl<AstroFileRecordMapper, AstFileRecordEntity>
        implements AstroFileRecordService {
}
