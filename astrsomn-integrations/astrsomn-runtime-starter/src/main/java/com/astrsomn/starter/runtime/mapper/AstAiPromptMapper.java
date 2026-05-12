package com.astrsomn.starter.runtime.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AstAiPromptMapper extends BaseMapper<AiPromptEntity> {


    @Select("")
    String getLatestPromptContentByPromptKey(String s);
}
