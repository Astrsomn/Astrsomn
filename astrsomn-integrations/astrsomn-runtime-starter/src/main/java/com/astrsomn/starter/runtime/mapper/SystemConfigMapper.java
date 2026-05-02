package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.config.SystemConfigResponseDTO;
import com.astrsomn.api.runtime.common.entity.SystemConfigEntity;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigEntity> {

    IPage<SystemConfigResponseDTO> queryPage(IPage<SystemConfigResponseDTO> page,
                                             @Param("req") SystemConfigQueryRequestDTO param);
}
