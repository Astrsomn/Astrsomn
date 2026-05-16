package com.astrsomn.starter.runtime.mapper;

import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AstAiPromptMapper extends BaseMapper<AiPromptEntity> {


    @Select("SELECT AP.PROMPT_CONTENT FROM AI_PROMPT AP WHERE AP.DELETED = 0 AND AP.PROMPT_KEY = #{promptKey} ORDER BY AP.VERSION DESC, AP.ID DESC LIMIT 1")
    String getLatestPromptContentByPromptKey(@Param("promptKey") String promptKey);
}
