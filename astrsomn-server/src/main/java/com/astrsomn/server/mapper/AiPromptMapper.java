package com.astrsomn.server.mapper;

import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {

    IPage<AiPromptResponseDTO> queryPage(IPage<AiPromptResponseDTO> page, @Param("req") AiPromptQueryRequestDTO param);

    String getByUUID(@Param("promptUuid") String promptUuid);


    List<AiPromptResponseDTO> listHistoryByPromptKey(@Param("promptKey") String promptKey, @Param("envCode") String envCode);

    String getLatestPromptContentByPromptKey(@Param("promptKey") String promptKey);

    List<String> querySceneRawList(@Param("envCode") String envCode);
}
