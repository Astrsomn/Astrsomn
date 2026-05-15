package com.astrsomn.server.mapper;

import com.astrsomn.system.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.system.dto.config.SystemConfigResponseDTO;
import com.astrsomn.system.entity.SystemConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigEntity> {

    IPage<SystemConfigResponseDTO> queryPage(IPage<SystemConfigResponseDTO> page,
                                             @Param("req") SystemConfigQueryRequestDTO param);
}
