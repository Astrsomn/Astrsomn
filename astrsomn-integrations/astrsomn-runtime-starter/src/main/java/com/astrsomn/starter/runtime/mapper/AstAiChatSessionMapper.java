package com.astrsomn.starter.runtime.mapper;

import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.api.runtime.common.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AstAiChatSessionMapper extends BaseMapper<AiChatSessionEntity> {

}
