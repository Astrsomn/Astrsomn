package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.env.SystemEnvQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.env.SystemEnvResponseDTO;
import com.astrsomn.api.runtime.common.entity.SystemEnvEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemEnvMapper extends BaseMapper<SystemEnvEntity> {

    IPage<SystemEnvResponseDTO> queryPage(IPage<SystemEnvResponseDTO> page, @Param("req") SystemEnvQueryRequestDTO param);
}
