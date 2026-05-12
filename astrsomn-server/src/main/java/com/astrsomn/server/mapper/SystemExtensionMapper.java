package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemExtensionMapper extends BaseMapper<SystemExtensionEntity> {

    IPage<SystemExtensionResponseDTO> queryPage(IPage<SystemExtensionResponseDTO> page,
                                                @Param("req") SystemExtensionQueryRequestDTO param);
}
