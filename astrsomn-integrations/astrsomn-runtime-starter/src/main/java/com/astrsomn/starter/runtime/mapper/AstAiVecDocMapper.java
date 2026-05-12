package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiVecDocEntity;

@Mapper
public interface AstAiVecDocMapper extends BaseMapper<AiVecDocEntity> {


}
