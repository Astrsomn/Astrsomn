package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

@Mapper
public interface AstAiVecSourceMapper extends BaseMapper<AiVecSourceEntity> {


}
