package com.astrsomn.server.mapper;

import com.astrsomn.system.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.system.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemExtensionMapper extends BaseMapper<SystemExtensionEntity> {

    IPage<SystemExtensionResponseDTO> queryPage(IPage<SystemExtensionResponseDTO> page,
                                                @Param("req") SystemExtensionQueryRequestDTO param);
}
