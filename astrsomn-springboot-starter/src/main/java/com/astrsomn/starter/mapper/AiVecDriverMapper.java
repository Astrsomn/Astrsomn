package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdriver.AiVecDriverResponseDTO;
import com.astrsomn.core.common.entity.AiVecDriverEntity;

@Mapper
public interface AiVecDriverMapper extends BaseMapper<AiVecDriverEntity> {
    IPage<AiVecDriverResponseDTO> queryPage(IPage<AiVecDriverResponseDTO> page, @Param("req") AiVecDriverQueryRequestDTO param);
}
