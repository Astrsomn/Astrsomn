package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.api.runtime.common.entity.SystemMessageEntity;

@Mapper
public interface AstSystemMessageMapper extends BaseMapper<SystemMessageEntity> {


}
