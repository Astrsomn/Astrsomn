package com.astrsomn.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.core.common.entity.AiPromptEntity;

import java.util.List;

@Mapper
public interface AiPromptMapper extends BaseMapper<AiPromptEntity> {

    IPage<AiPromptResponseDTO> queryPage(IPage<AiPromptResponseDTO> page, @Param("req") AiPromptQueryRequestDTO param);

    String getByUUID(@Param("promptUuid") String promptUuid);

    /**
     * 同一 promptKey（及环境）下全部历史版本，按版本号倒序。
     */
    List<AiPromptResponseDTO> listHistoryByPromptKey(@Param("promptKey") String promptKey, @Param("envCode") String envCode);
}
