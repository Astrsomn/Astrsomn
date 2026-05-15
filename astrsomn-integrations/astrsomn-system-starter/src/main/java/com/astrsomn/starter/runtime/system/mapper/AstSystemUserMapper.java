package com.astrsomn.starter.runtime.system.mapper;

import com.astrsomn.system.entity.SystemUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AstSystemUserMapper extends BaseMapper<SystemUserEntity> {
}
