package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.core.common.entity.SystemMessageEntity;

@Mapper
public interface SystemMessageMapper extends BaseMapper<SystemMessageEntity> {

    IPage<SystemMessageResponseDTO> queryPage(
            IPage<SystemMessageResponseDTO> page, @Param("req") SystemMessageQueryRequestDTO param);
}
