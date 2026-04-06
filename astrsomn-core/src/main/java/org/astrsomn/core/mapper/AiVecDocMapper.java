package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import org.astrsomn.core.common.entity.AiVecDocEntity;

@Mapper
public interface AiVecDocMapper extends BaseMapper<AiVecDocEntity> {

    IPage<AiVecDocResponseDTO> queryPage(IPage<AiVecDocResponseDTO> page, @Param("req") AiVecDocQueryRequestDTO param);
}
