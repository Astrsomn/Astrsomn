package com.astrsomn.server.mapper;

import com.astrsomn.api.vector.dto.vecdoc.AiVecDocQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecdoc.AiVecDocResponseDTO;
import com.astrsomn.api.vector.entity.AiVecDocEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiVecDocMapper extends BaseMapper<AiVecDocEntity> {

    IPage<AiVecDocResponseDTO> queryPage(IPage<AiVecDocResponseDTO> page, @Param("req") AiVecDocQueryRequestDTO param);
}
