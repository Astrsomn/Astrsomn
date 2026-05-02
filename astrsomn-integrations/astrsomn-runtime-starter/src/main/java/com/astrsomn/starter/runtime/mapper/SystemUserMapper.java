package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.user.SystemUserQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.user.SystemUserResponseDTO;
import com.astrsomn.api.runtime.common.entity.SystemUserEntity;

@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUserEntity> {

    IPage<SystemUserResponseDTO> queryPage(IPage<SystemUserResponseDTO> page, @Param("req") SystemUserQueryRequestDTO param);
}
