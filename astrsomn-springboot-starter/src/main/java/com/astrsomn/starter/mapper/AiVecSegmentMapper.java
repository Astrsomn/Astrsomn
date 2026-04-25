package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.core.common.entity.AiVecSegmentEntity;

@Mapper
public interface AiVecSegmentMapper extends BaseMapper<AiVecSegmentEntity> {

    IPage<AiVecSegmentResponseDTO> queryPage(IPage<AiVecSegmentResponseDTO> page, @Param("req") AiVecSegmentQueryRequestDTO param);
}
