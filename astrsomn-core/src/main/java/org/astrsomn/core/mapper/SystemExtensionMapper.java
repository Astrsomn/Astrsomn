package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.entity.SystemExtensionEntity;

@Mapper
public interface SystemExtensionMapper extends BaseMapper<SystemExtensionEntity> {

    IPage<SystemExtensionResponseDTO> queryPage(IPage<SystemExtensionResponseDTO> page,
                                                @Param("req") SystemExtensionQueryRequestDTO param);
}
