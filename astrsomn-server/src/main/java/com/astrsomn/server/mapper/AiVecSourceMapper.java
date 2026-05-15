package com.astrsomn.server.mapper;

import com.astrsomn.api.vector.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiVecSourceMapper extends BaseMapper<AiVecSourceEntity> {

    IPage<AiVecSourceResponseDTO> queryPage(IPage<AiVecSourceResponseDTO> page, @Param("req") AiVecSourceQueryRequestDTO param);
}
