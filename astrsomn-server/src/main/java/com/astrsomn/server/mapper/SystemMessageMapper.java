package com.astrsomn.server.mapper;

import com.astrsomn.system.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.system.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.system.entity.SystemMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemMessageMapper extends BaseMapper<SystemMessageEntity> {

    IPage<SystemMessageResponseDTO> queryPage(
            IPage<SystemMessageResponseDTO> page, @Param("req") SystemMessageQueryRequestDTO param);
}
