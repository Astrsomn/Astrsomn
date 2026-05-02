package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiVecSegmentEntity;

@Mapper
public interface AiVecSegmentMapper extends BaseMapper<AiVecSegmentEntity> {

    IPage<AiVecSegmentResponseDTO> queryPage(IPage<AiVecSegmentResponseDTO> page, @Param("req") AiVecSegmentQueryRequestDTO param);
}
