package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import com.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import com.astrsomn.core.common.entity.SystemExtensionEntity;

@Mapper
public interface SystemExtensionMapper extends BaseMapper<SystemExtensionEntity> {

    IPage<SystemExtensionResponseDTO> queryPage(IPage<SystemExtensionResponseDTO> page,
                                                @Param("req") SystemExtensionQueryRequestDTO param);
}
