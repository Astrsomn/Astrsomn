package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.entity.SystemEnvEntity;
import org.astrsomn.core.mapper.SystemEnvMapper;
import org.astrsomn.server.service.SystemEnvService;
import org.springframework.stereotype.Service;

@Service
public class SystemEnvServiceImpl extends ServiceImpl<SystemEnvMapper, SystemEnvEntity> implements SystemEnvService {
}
