package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import org.astrsomn.core.common.entity.AiToolEntity;

@Mapper
public interface AiToolMapper extends BaseMapper<AiToolEntity> {

    IPage<AiToolResponseDTO> queryPage(IPage<AiToolResponseDTO> page, @Param("req") AiToolQueryRequestDTO param);
}
