package com.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.core.common.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.core.common.entity.AiVecSourceEntity;

@Mapper
public interface AiVecSourceMapper extends BaseMapper<AiVecSourceEntity> {

    IPage<AiVecSourceResponseDTO> queryPage(IPage<AiVecSourceResponseDTO> page, @Param("req") AiVecSourceQueryRequestDTO param);
}
