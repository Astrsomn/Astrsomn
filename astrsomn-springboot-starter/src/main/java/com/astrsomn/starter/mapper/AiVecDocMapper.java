package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.core.common.entity.AiVecDocEntity;

@Mapper
public interface AiVecDocMapper extends BaseMapper<AiVecDocEntity> {

    IPage<AiVecDocResponseDTO> queryPage(IPage<AiVecDocResponseDTO> page, @Param("req") AiVecDocQueryRequestDTO param);
}
