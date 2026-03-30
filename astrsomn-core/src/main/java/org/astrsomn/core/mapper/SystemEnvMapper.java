package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.env.SystemEnvQueryRequestDTO;
import org.astrsomn.core.common.dto.env.SystemEnvResponseDTO;
import org.astrsomn.core.common.entity.SystemEnvEntity;

@Mapper
public interface SystemEnvMapper extends BaseMapper<SystemEnvEntity> {

    IPage<SystemEnvResponseDTO> queryPage(IPage<SystemEnvResponseDTO> page, @Param("req") SystemEnvQueryRequestDTO param);
}
