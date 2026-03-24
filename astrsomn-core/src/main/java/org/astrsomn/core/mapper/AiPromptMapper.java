package org.astrsomn.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import org.astrsomn.core.common.entity.AiPromptEntity;

@Mapper
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {

    IPage<AiPromptResponseDTO> queryPage(IPage<AiPromptResponseDTO> page, @Param("req") AiPromptQueryRequestDTO param);

    String getByUUID(@Param("promptUuid") String promptUuid);
}
