package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.user.SystemUserQueryRequestDTO;
import org.astrsomn.core.common.dto.user.SystemUserResponseDTO;
import org.astrsomn.core.common.entity.SystemUserEntity;

@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUserEntity> {

    IPage<SystemUserResponseDTO> queryPage(IPage<SystemUserResponseDTO> page, @Param("req") SystemUserQueryRequestDTO param);
}
