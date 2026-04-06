package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;

@Mapper
public interface AiVecSegmentMapper extends BaseMapper<AiVecSegmentEntity> {

    IPage<AiVecSegmentResponseDTO> queryPage(IPage<AiVecSegmentResponseDTO> page, @Param("req") AiVecSegmentQueryRequestDTO param);
}
