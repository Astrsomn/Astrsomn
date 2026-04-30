package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.config.SystemConfigQueryRequestDTO;
import com.astrsomn.core.common.dto.config.SystemConfigResponseDTO;
import com.astrsomn.core.common.entity.SystemConfigEntity;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigEntity> {

    IPage<SystemConfigResponseDTO> queryPage(IPage<SystemConfigResponseDTO> page,
                                             @Param("req") SystemConfigQueryRequestDTO param);
}
