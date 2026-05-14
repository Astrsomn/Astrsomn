package com.astrsomn.starter.runtime.mapper;

import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AstAiPromptMapper extends BaseMapper<AiPromptEntity> {


    @Select("")
    String getLatestPromptContentByPromptKey(String s);
}
