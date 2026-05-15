package com.astrsomn.server.mapper;

import com.astrsomn.system.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.system.dto.user.SystemUserResponseDTO;
import com.astrsomn.system.entity.SystemUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUserEntity> {

    IPage<SystemUserResponseDTO> queryPage(IPage<SystemUserResponseDTO> page, @Param("req") SystemUserQueryRequestDTO param);
}
